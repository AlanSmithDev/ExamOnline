package com.sdut.examonline.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdut.examonline.common.constant.AliOssConstants;
import com.sdut.examonline.common.constant.JwtConstants;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.*;
import com.sdut.examonline.dubbo.client.*;
import com.sdut.examonline.user.dto.*;
import com.sdut.examonline.user.entity.User;
import com.sdut.examonline.user.mapper.UserMapper;
import com.sdut.examonline.user.service.UserService;
import com.sdut.examonline.user.util.BaiduFaceUtils;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.user.vo.UserVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.ExcelUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    private static final SimpleDateFormat BIRTHDAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String DEFAULT_USER_PASSWORD = "123456789a";

    private static final Map<String, String> DEV_TOKEN = new HashMap<String, String>() {{
        put("token-admin-web", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzMyIsInVzZXJfbmFtZSI6ImFkbWluIiwicm9sZSI6ImFkbWluIiwicGxhdGZvcm0iOjAsImlhdCI6MTY0OTQyNDM4MSwiZXhwIjoyMDA5NDI0MzgxfQ.UbljPqiqZkPfxsMXvytyJefxZsoKfuUrxsc6j_yCgGlPYas0nLxsUlfX6P46dDqjoyHBnaD24sg3Feyj7B4o6A");
        put("token-teacher-web", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzNCIsInVzZXJfbmFtZSI6InRlYWNoZXIiLCJyb2xlIjoidGVhY2hlciIsInBsYXRmb3JtIjowLCJpYXQiOjE2NDk0MjQ0MjIsImV4cCI6MjAwOTQyNDQyMn0.C7GrLEcjONEzleV1TYIORNoB5kDp6LVo7SWpPtW2KCV1hYn6fmZuP380SeLk9M5XbwySYDFdczvS1hkiCWT4PQ");
        put("token-student-web", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzNiIsInVzZXJfbmFtZSI6InN0dWRlbnQiLCJyb2xlIjoic3R1ZGVudCIsInBsYXRmb3JtIjowLCJpYXQiOjE2NDk0MjUwNDMsImV4cCI6MjAwOTQyNTA0M30.mcn-Koe5OTRHAG2UsJxokZNf2x37VUcFROrOLCfvY9jLfMb4PbBKhM3gX531of0pUdjaxamm2qDIllc9k_KLRw");
        put("token-admin-app", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzMyIsInVzZXJfbmFtZSI6ImFkbWluIiwicm9sZSI6ImFkbWluIiwicGxhdGZvcm0iOjEsImlhdCI6MTY0ODA0NzgwNiwiZXhwIjoyMDA4MDQ3ODA2fQ.uDfIytZ6zQ9Srcd3VDkdaBEFr7msRRku550CyMCeDnl2iUV2IpF0l0muXsiquJRYe0Qpc4on_nacIUs0oMzQmw");
        put("token-teacher-app", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzNCIsInVzZXJfbmFtZSI6InRlYWNoZXIiLCJyb2xlIjoidGVhY2hlciIsInBsYXRmb3JtIjoxLCJpYXQiOjE2NDgxMTkxNTIsImV4cCI6MjAwODExOTE1Mn0.BaIbkY8TitmVHP57Lp6F1Aq3hcQzponXGbPQ--vMjjluJ6fjm59vs9Pr64-H3ZI1YsaNHAOYk1GmgEEOG6WEgA");
        put("token-student-app", "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMzk1MzQ5Njk1NzIwMjQzNiIsInVzZXJfbmFtZSI6InN0dWRlbnQiLCJyb2xlIjoic3R1ZGVudCIsInBsYXRmb3JtIjoxLCJpYXQiOjE2NDgxMTkxODIsImV4cCI6MjAwODExOTE4Mn0.vuQNtt1CHpKuqcpKitUwVJ9BOrg1EwnrjaTYmWUGs7uEx9IBSm2mYW1ZW73f0KZ8IL-fia104UXs8RVaMrIQnw");
    }};

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    CacheUtils cacheUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Reference(check = false)
    DepartDubboService departDubboService;

    @Reference(check = false)
    ExamDubboService examDubboService;

    @Reference(check = false)
    LogDubboService logDubboService;

    @Reference(check = false)
    MessageDubboService messageDubboService;

    @Reference(check = false)
    PaperDubboService paperDubboService;

    @Reference(check = false)
    QuestionDubboService questionDubboService;

    @Reference(check = false)
    UserDubboService userDubboService;

    @Override
    public Map<String, Object> dashboard() {
        String userRole = JwtUtils.getUserRole();
        Map<String, Object> map = Maps.newHashMap();
        final long timeout = 5;
        final TimeUnit unit = TimeUnit.SECONDS;
        if (RoleConstants.ADMIN.equals(userRole)) {
            try {
                CompletableFuture<Map<String, Object>> futureDepart = FutureUtils.timeout(taskExecutor, () -> departDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futureExam = FutureUtils.timeout(taskExecutor, () -> examDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futureLog = FutureUtils.timeout(taskExecutor, () -> logDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futureMessage = FutureUtils.timeout(taskExecutor, () -> messageDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futurePaper = FutureUtils.timeout(taskExecutor, () -> paperDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futureQuestion = FutureUtils.timeout(taskExecutor, () -> questionDubboService.getAdminDashboard(), timeout, unit);
                CompletableFuture<Map<String, Object>> futureUser = FutureUtils.timeout(taskExecutor, () -> userDubboService.getAdminDashboard(), timeout, unit);
                try {
                    map.putAll(futureDepart.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futureExam.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futureLog.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futureMessage.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futurePaper.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futureQuestion.get());
                } catch (Exception ignored) {
                }
                try {
                    map.putAll(futureUser.get());
                } catch (Exception ignored) {
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new ServiceException(ErrorCode.INNER_ERROR);
            }
        } else if (RoleConstants.TEACHER.equals(userRole)) {

        } else if (RoleConstants.STUDENT.equals(userRole)) {

        }
        return map;
    }

    @Override
    public UserLoginVo login(UserLoginRequest loginRequest) {
        User user = getUserByUserName(loginRequest.getUsername());

        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        String decrypted = AesUtils.decrypt(loginRequest.getPassword());
        Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");
        Assert.isTrue(passwordEncoder.matches(decrypted, user.getPassword()), ErrorCode.USER_PASSWORD_NOT_MATCH);

        UserLoginVo loginVo = new UserLoginVo();
        MapperUtils.map(user, loginVo);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUserName(), String.valueOf(user.getId()), Lists.newArrayList(new SimpleGrantedAuthority(user.getRole())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        loginVo.setToken(generateToken(user, loginRequest.getPlatform()).getA());
        return loginVo;
    }

    @Override
    public UserLoginVo register(UserRegisterRequest registerRequest) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName, registerRequest.getUsername());

        Assert.isNull(super.getOne(wrapper, false), ErrorCode.USER_NAME_DUPLICATE);

        User user = new User();
        user.setId(SnowFlakeIdWorker.nextId());
        user.setUserName(registerRequest.getUsername());
        user.setRealName(registerRequest.getRealName());
        String decrypted = AesUtils.decrypt(registerRequest.getPassword());
        Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");
        user.setPassword(passwordEncoder.encode(decrypted));
        user.setRole(RoleConstants.STUDENT);
        if (StringUtils.isNotEmpty(registerRequest.getBirthday())) {
            user.setBirthday(registerRequest.getBirthday());
        }
        user.setGender(registerRequest.getGender());
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setState(0);

        super.save(user);
        cacheUtils.set(getCacheKeyUserId(user.getId()), user, 10L, TimeUnit.MINUTES);
        cacheUtils.set(getCacheKeyUserName(user.getUserName()), user, 10L, TimeUnit.MINUTES);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUserName(), String.valueOf(user.getId()), Lists.newArrayList(new SimpleGrantedAuthority(user.getRole())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserLoginVo loginVo = new UserLoginVo();
        MapperUtils.map(user, loginVo);
        loginVo.setToken(generateToken(user, registerRequest.getPlatform()).getA());
        return loginVo;
    }

    @Override
    public UserVo save(UserSaveRequest saveRequest) {
        Long userId = JwtUtils.getUserId();
        String role = JwtUtils.getUserRole();
        User user = getUserByIdInner(userId);

        if (StringUtils.isNotEmpty(saveRequest.getRealName())) {
            user.setRealName(saveRequest.getRealName());
        }

        if (StringUtils.isNotEmpty(saveRequest.getRole())) {
            saveRequest.setRole(saveRequest.getRole().toLowerCase());
            Assert.isTrue(RoleConstants.ADMIN.equalsIgnoreCase(role), ErrorCode.FORBIDDEN);
            user.setRole(saveRequest.getRole());
        }

        if (StringUtils.isNotEmpty(saveRequest.getBirthday())) {
            try {
                Date time = BIRTHDAY_DATE_FORMAT.parse(saveRequest.getBirthday());
                user.setBirthday(BIRTHDAY_DATE_FORMAT.format(time));
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.USER_BIRTHDAY_INVALID);
            }
        }

        if (saveRequest.getGender() != 0) {
            user.setGender(saveRequest.getGender());
        }

        user.setUpdateTime(new Date());
        super.updateById(user);
        cacheUtils.delete(getCacheKeyUserId(user.getId()));
        cacheUtils.delete(getCacheKeyUserName(user.getUserName()));

        UserVo userVo = new UserVo();
        MapperUtils.map(user, userVo);
        return userVo;
    }

    @Override
    public Boolean changePassword(UserChangePasswordRequest changePasswordRequest) {
        Long userId = JwtUtils.getUserId();
        User user = getUserByIdInner(userId);

        if (StringUtils.isNotEmpty(changePasswordRequest.getPassword())) {

            String decrypted = AesUtils.decrypt(changePasswordRequest.getPassword());
            String decryptedNew = AesUtils.decrypt(changePasswordRequest.getNewPassword());
            Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "原密码长度为5-20之间");
            Assert.isTrue(decryptedNew != null && decryptedNew.length() >= 5 && decryptedNew.length() <= 20, 400, "新密码长度为5-20之间");
            Assert.isTrue(passwordEncoder.matches(decrypted, user.getPassword()), ErrorCode.USER_PASSWORD_NOT_MATCH);
            user.setPassword(passwordEncoder.encode(decryptedNew));
            user.setUpdateTime(new Date());
            boolean result = super.updateById(user);
            cacheUtils.delete(getCacheKeyUserId(user.getId()));
            cacheUtils.delete(getCacheKeyUserName(user.getUserName()));
            return result;
        }
        return false;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Long userId = JwtUtils.getUserId();
        User user = getUserByIdInner(userId);

        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        if (StringUtils.isNotEmpty(user.getAvatar())) {
            String lastAvatar = user.getAvatar().substring(user.getAvatar().lastIndexOf('/') + 1);
            if (AliOssUtils.isFileExists(lastAvatar)) {
                AliOssUtils.deleteFile(lastAvatar);
            }
        }
        user.setAvatar(null);

        String ext = "";
        if (!StringUtils.isEmpty(file.getOriginalFilename())) {
            ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        }

        String fullName = "avatar_" + RandomStringUtils.randomAlphabetic(16) + (StringUtils.isEmpty(ext) ? "" : '.' + ext);
        try {
            if (AliOssUtils.isFileExists(fullName)) {
                AliOssUtils.deleteFile(fullName);
            }
            AliOssUtils.uploadFile(fullName, file.getInputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }

        user.setUpdateTime(new Date());
        user.setAvatar(AliOssConstants.DOWNLOAD_URL + '/' + fullName);
        super.updateById(user);
        cacheUtils.delete(getCacheKeyUserId(user.getId()));
        cacheUtils.delete(getCacheKeyUserName(user.getUserName()));

        return user.getAvatar();
    }

    @Override
    public Boolean uploadFace(MultipartFile file) {
        try {
            Long userId = JwtUtils.getUserId();
            if (BaiduFaceUtils.isFaceExists(userId)) {
                String encoded = Base64.getEncoder().encodeToString(file.getBytes());
                String userIdStr = BaiduFaceUtils.checkFace(encoded);
                if (StringUtils.isEmpty(userIdStr) || !userId.equals(Long.parseLong(userIdStr))) {
                    throw new ServiceException(400, "人脸认证失败");
                }
                return BaiduFaceUtils.updateFace(encoded, userId);
            }
            return BaiduFaceUtils.registerFace(Base64.getEncoder().encodeToString(file.getBytes()), userId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
    }

    @Override
    public UserVo current() {
        Long userId = JwtUtils.getUserId();
        User user = getUserByIdInner(userId);

        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        UserVo userVo = new UserVo();
        MapperUtils.map(user, userVo);
        return userVo;
    }

    @Override
    public void logout(String token) {
        if (DEV_TOKEN.containsKey(token)) {
            return;
        }
        cacheUtils.delete(getCacheKeyToken(token));
    }

    @Override
    public String getInternalToken(String accessToken) {
        if (DEV_TOKEN.containsKey(accessToken)) {
            return DEV_TOKEN.get(accessToken);
        }
        String value = cacheUtils.getString(getCacheKeyToken(accessToken), JwtConstants.EXPIRATION, TimeUnit.MILLISECONDS);
        if (cacheUtils.isNullString(value) || StringUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }

    @Override
    public PagingVo<UserVo> page(PagingDto pagingDto) {
        IPage<User> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        IPage<User> page = super.page(query);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), UserVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUserName(username);
        if (user == null) {
            return null;
        }
        return org.springframework.security.core.userdetails.User.withUsername(username).password(user.getPassword())
                .authorities(user.getRole()).build();
    }

    @Override
    public User getUserByIdInner(Long userId) {
        Object value = cacheUtils.get(getCacheKeyUserId(userId), User.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
        if (value != null) {
            return (User) value;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getId, userId);
        User user = super.getOne(wrapper, false);
        if (user != null) {
            cacheUtils.set(getCacheKeyUserId(user.getId()), user, 10, TimeUnit.MINUTES);
            cacheUtils.set(getCacheKeyUserName(user.getUserName()), user, 10, TimeUnit.MINUTES);
        }
        return user;
    }

    @Override
    public List<User> getUserByIdBatch(List<Long> userIds) {
        List<User> list = Lists.newArrayList();
        for (Long userId : userIds) {
            Object value = cacheUtils.get(getCacheKeyUserId(userId), User.class, 10, TimeUnit.MINUTES);
            if (value instanceof User) {
                list.add((User) value);
            }
        }
        for (User user : list) {
            userIds.remove(user.getId());
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(User::getId, userIds);
            List<User> users = super.list(wrapper);
            for (User user : users) {
                cacheUtils.set(getCacheKeyUserId(user.getId()), user, 10, TimeUnit.MINUTES);
                cacheUtils.set(getCacheKeyUserName(user.getUserName()), user, 10, TimeUnit.MINUTES);
            }
            list.addAll(users);
        }
        return list;
    }

    @Override
    public List<UserImportResult> importFromExcel(InputStream inputStream) {
        List<UserImportDto> users = ExcelUtils.readExcel(inputStream, UserImportDto.class);
        List<UserImportResult> results = Lists.newArrayList();
        for (UserImportDto user : users) {
            UserImportResult result = new UserImportResult();
            results.add(result);
            MapperUtils.map(user, result);
            try {
                Date time = BIRTHDAY_DATE_FORMAT.parse(user.getBirthday());
                user.setBirthday(BIRTHDAY_DATE_FORMAT.format(time));
            } catch (Exception e) {
                user.setBirthday(null);
            }
            if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getRealName())) {
                result.setResult("用户名或学号填写错误");
            }
        }
        final String password = passwordEncoder.encode(DEFAULT_USER_PASSWORD);
        final Date now = new Date();
        CountDownLatch countDownLatch = new CountDownLatch(results.size());
        for (final UserImportResult result : results) {
            taskExecutor.execute(() -> {
                try {
                    User user = null;
                    try {
                        user = getUserByUserName(result.getUserName());
                    } catch (Exception ignored) {
                    }
                    if (user == null) {
                        user = new User();
                        user.setId(SnowFlakeIdWorker.nextId());
                        user.setUserName(result.getUserName());
                        user.setRealName(result.getRealName());
                        user.setPassword(password);
                        user.setRole(RoleConstants.STUDENT);
                        user.setBirthday(result.getBirthday());
                        user.setGender(result.getGender());
                        user.setCreateTime(now);
                        user.setUpdateTime(now);
                        user.setState(0);

                        super.save(user);

                        result.setPassword(DEFAULT_USER_PASSWORD);
                        result.setResult("导入成功");
                    } else {
                        result.setPassword(StringUtils.EMPTY);
                        result.setResult("学号已存在");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
        return results;
    }

    @Override
    public Boolean forceChangePassword(UserAdminChangePasswordRequest request) {
        User user = getUserByIdInner(request.getUserId());
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        String decrypted = AesUtils.decrypt(request.getPassword());
        Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");
        user.setPassword(passwordEncoder.encode(decrypted));

        super.updateById(user);

        cacheUtils.delete(getCacheKeyUserId(user.getId()));
        cacheUtils.delete(getCacheKeyUserName(user.getUserName()));

        kickAllLoggedIn(user.getId());

        return true;
    }

    @Override
    public Boolean forceChangeRole(UserAdminChangeRoleRequest request) {
        User user = getUserByIdInner(request.getUserId());
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        if (user.getId().equals(JwtUtils.getUserId())) {
            throw new ServiceException(400, "无法修改自己的身份");
        }

        if (!RoleConstants.STUDENT.equals(request.getRole()) && !RoleConstants.TEACHER.equals(request.getRole())) {
            throw new ServiceException(400, "未知的身份枚举");
        }

        user.setRole(request.getRole());
        super.updateById(user);

        cacheUtils.delete(getCacheKeyUserId(user.getId()));
        cacheUtils.delete(getCacheKeyUserName(user.getUserName()));

        kickAllLoggedIn(user.getId());

        return true;
    }

    @Override
    public Tuple<String, String> generateToken(User user, Integer platform) {
        String accessToken = UUID.randomUUID().toString();
        String internalToken = JwtTokenUtils.createToken(user.getId(), user.getUserName(), user.getRole(), platform);
        cacheUtils.setString(getCacheKeyToken(accessToken), internalToken);
        cacheUtils.getRedisTemplate().opsForSet().add(getCacheKeyTokens(user.getId()), accessToken);
        return Tuple.of(accessToken, internalToken);
    }

    private User getUserByUserName(String userName) {
        Object value = cacheUtils.get(getCacheKeyUserName(userName.toLowerCase()), User.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
        if (value != null) {
            return (User) value;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName, userName);
        User user = super.getOne(wrapper, false);
        if (user != null) {
            cacheUtils.set(getCacheKeyUserId(user.getId()), user, 10, TimeUnit.MINUTES);
            cacheUtils.set(getCacheKeyUserName(user.getUserName()), user, 10, TimeUnit.MINUTES);
        }
        return user;
    }

    private void kickAllLoggedIn(Long userId) {
        Set<String> members = cacheUtils.getRedisTemplate().opsForSet().members(getCacheKeyTokens(userId));
        if (CollectionUtils.isNotEmpty(members)) {
            try {
                for (String accessToken : members) {
                    cacheUtils.delete(getCacheKeyToken(accessToken));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                cacheUtils.delete(getCacheKeyTokens(userId));
            }
        }
    }

    private String getCacheKeyTokens(Long userId) {
        return "tokens:" + userId;
    }

    private String getCacheKeyToken(String token) {
        return "token:" + token;
    }

    private String getCacheKeyUserId(Long userId) {
        return "user:id:" + userId;
    }

    private String getCacheKeyUserName(String userName) {
        return "user:name:" + userName.toLowerCase();
    }

}
