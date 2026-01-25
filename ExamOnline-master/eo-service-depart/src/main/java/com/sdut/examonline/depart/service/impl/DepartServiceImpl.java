package com.sdut.examonline.depart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.depart.dto.*;
import com.sdut.examonline.depart.entity.Depart;
import com.sdut.examonline.depart.entity.DepartMember;
import com.sdut.examonline.depart.mapper.DepartMapper;
import com.sdut.examonline.depart.service.DepartMemberService;
import com.sdut.examonline.depart.service.DepartService;
import com.sdut.examonline.depart.vo.DepartDetailVo;
import com.sdut.examonline.depart.vo.DepartMemberVo;
import com.sdut.examonline.depart.vo.DepartVo;
import com.sdut.examonline.dubbo.client.UserDubboService;
import com.sdut.examonline.dubbo.dto.DepartMembersDto;
import com.sdut.examonline.dubbo.dto.UserDto;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DepartServiceImpl extends ServiceImpl<DepartMapper, Depart> implements DepartService {

    @Autowired
    CacheUtils cacheUtils;

    @Autowired
    DepartMemberService departMemberService;

    @Reference(check = false)
    UserDubboService userDubboService;

    @Override
    public DepartVo add(DepartCreateRequest createRequest) {
        Depart depart = new Depart();
        depart.setId(SnowFlakeIdWorker.nextId());

        depart.setCode(RandomStringUtils.randomNumeric(8));
        MapperUtils.map(createRequest, depart);

        Date now = new Date();
        depart.setUpdateTime(now);
        depart.setCreateTime(now);

        super.save(depart);

        cacheUtils.set(getCacheKeyDepart(depart.getId()), depart, 10L, TimeUnit.MINUTES);

        DepartVo departVo = new DepartVo();
        MapperUtils.map(depart, departVo);

        return departVo;
    }

    @Override
    public DepartVo save(DepartSaveRequest saveRequest) {
        Depart depart = getDepartInner(saveRequest.getId());
        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        MapperUtils.map(saveRequest, depart);
        depart.setUpdateTime(new Date());
        super.updateById(depart);

        removeCahche(depart.getId(), depart.getCode());

        DepartVo departVo = new DepartVo();
        MapperUtils.map(depart, departVo);

        return departVo;
    }

    @Override
    public Boolean delete(Long departId) {
        DepartVo depart = getDepart(departId);
        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        LambdaQueryWrapper<Depart> departWrapper = new LambdaQueryWrapper<>();
        departWrapper.eq(Depart::getId, depart.getId());
        boolean departRemoved = super.remove(departWrapper);

        removeCahche(depart.getId(), depart.getCode());

        LambdaQueryWrapper<DepartMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartMember::getDepartId, depart.getId());
        boolean membersRemoved = departMemberService.remove(wrapper);

        return departRemoved || membersRemoved;
    }

    @Override
    public DepartVo getDepart(Long departId) {
        Depart depart = getDepartInner(departId);
        DepartVo departVo = new DepartVo();
        MapperUtils.map(depart, departVo);
        return departVo;
    }

    @Override
    public DepartVo join(String code) {
        Depart depart = getDepartByCodeInner(code);
        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        Long userId = JwtUtils.getUserId();
        LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(DepartMember::getDepartId, depart.getId())
                .eq(DepartMember::getUserId, userId);

        DepartMember departMember = departMemberService.getOne(memberWrapper, false);

        Assert.isNull(departMember, ErrorCode.DEPART_ALREADY_JOINED);

        departMember = new DepartMember();
        departMember.setId(SnowFlakeIdWorker.nextId());
        departMember.setDepartId(depart.getId());
        departMember.setUserId(userId);
        departMemberService.save(departMember);

        return MapperUtils.map(depart, DepartVo.class);
    }

    @Override
    public List<DepartVo> myDepart() {
        return getDepartsByUserId(JwtUtils.getUserId());
    }

    @Override
    public List<DepartVo> getDepartsByUserId(Long userId) {
        LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(DepartMember::getUserId, userId);
        List<DepartMember> members = departMemberService.list(memberWrapper);

        List<Long> ids = Lists.newArrayList();
        for (DepartMember member : members) {
            ids.add(member.getDepartId());
        }

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Depart::getId, ids);
        return MapperUtils.mapList(super.list(wrapper), DepartVo.class);
    }

    @Override
    public List<Long> getDepartMemberIdsInner(Long departId) {
        LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(DepartMember::getDepartId, departId);
        List<DepartMember> list = departMemberService.list(memberWrapper);

        return list.stream().map(DepartMember::getUserId).collect(Collectors.toList());
    }

    @Override
    public PagingVo<DepartVo> page(PagingDto pagingDto) {
        IPage<Depart> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        IPage<Depart> page = super.page(query);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), DepartVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public List<DepartVo> all() {
        return MapperUtils.mapList(super.list(), DepartVo.class);
    }

    @Override
    public DepartDetailVo detail(Long departId) {
        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Depart::getId, departId);
        Depart depart = super.getOne(wrapper, false);

        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        DepartDetailVo detailVo = new DepartDetailVo();
        MapperUtils.map(depart, detailVo);

        List<Long> userIds = Lists.newArrayList();

        LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.select(DepartMember::getUserId).eq(DepartMember::getDepartId, departId);
        for (Object obj : departMemberService.listObjs(memberWrapper)) {
            userIds.add((Long) obj);
        }
        if (!userIds.isEmpty()) {
            detailVo.setMembers(MapperUtils.mapList(userDubboService.getUserByIdBatch(userIds), DepartMemberVo.class));
        } else {
            detailVo.setMembers(Lists.newArrayList());
        }
        return detailVo;
    }

    @Override
    public DepartMemberVo add(DepartMemberAddRequest request) {
        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Depart::getId, request.getId());
        Depart depart = super.getOne(wrapper, false);

        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        UserDto userDto = userDubboService.getUserById(request.getUserId());

        Assert.notNull(userDto, ErrorCode.USER_NOT_FOUND);

        DepartMember departMember = new DepartMember();
        departMember.setId(SnowFlakeIdWorker.nextId());
        departMember.setDepartId(request.getId());
        departMember.setUserId(request.getUserId());
        departMemberService.save(departMember);
        return MapperUtils.map(userDto, DepartMemberVo.class);
    }

    @Override
    public List<DepartMemberVo> addBatch(DepartMemberAddBatchRequest request) {
        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Depart::getId, request.getId());
        Depart depart = super.getOne(wrapper, false);

        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        List<DepartMember> members = Lists.newArrayList();
        List<UserDto> users = Lists.newArrayList();
        for (UserDto userDto : userDubboService.getUserByIdBatch(request.getUserIds())) {
            DepartMember departMember = new DepartMember();
            departMember.setId(SnowFlakeIdWorker.nextId());
            departMember.setDepartId(request.getId());
            departMember.setUserId(userDto.getId());
            users.add(userDto);
            members.add(departMember);
        }
        departMemberService.saveBatch(members);
        return MapperUtils.mapList(users, DepartMemberVo.class);
    }

    @Override
    public Boolean remove(DepartMemberRemoveRequest request) {
        LambdaQueryWrapper<DepartMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartMember::getDepartId, request.getId())
                .eq(DepartMember::getUserId, request.getUserId());
        return departMemberService.remove(wrapper);
    }

    @Override
    public Boolean removeBatch(DepartMemberRemoveBatchRequest request) {
        LambdaQueryWrapper<DepartMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartMember::getDepartId, request.getId())
                .in(DepartMember::getUserId, request.getUserIds());
        return departMemberService.remove(wrapper);
    }

    @Override
    public DepartMembersDto getMembersDubbo(Long departId) {
        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Depart::getId, departId);
        Depart depart = super.getOne(wrapper, false);

        Assert.notNull(depart, ErrorCode.DEPART_NOT_FOUND);

        DepartMembersDto membersDto = new DepartMembersDto();
        membersDto.setDepartId(depart.getId());
        membersDto.setDeptName(depart.getDeptName());
        List<Long> userIds = Lists.newArrayList();
        membersDto.setMembers(userIds);

        LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.select(DepartMember::getUserId).eq(DepartMember::getDepartId, departId);
        for (Object obj : departMemberService.listObjs(memberWrapper)) {
            userIds.add((Long) obj);
        }
        return membersDto;
    }

    @Override
    public Map<String, DepartMembersDto> getMembersBatchDubbo(List<Long> departIds) {
        Map<String, DepartMembersDto> map = Maps.newHashMap();
        LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Depart::getId, departIds);
        for (Depart depart : super.list(wrapper)) {
            DepartMembersDto membersDto = new DepartMembersDto();
            membersDto.setDepartId(depart.getId());
            membersDto.setDeptName(depart.getDeptName());
            List<Long> userIds = Lists.newArrayList();
            membersDto.setMembers(userIds);

            LambdaQueryWrapper<DepartMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.select(DepartMember::getUserId).eq(DepartMember::getDepartId, depart.getId());
            for (Object obj : departMemberService.listObjs(memberWrapper)) {
                userIds.add((Long) obj);
            }
            map.put(depart.getDeptName(), membersDto);
        }

        return map;
    }

    private Depart getDepartInner(Long departId) {
        Object value = cacheUtils.get(getCacheKeyDepart(departId), Depart.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.DEPART_NOT_FOUND);
        }
        if (value != null) {
            return (Depart) value;
        }
        QueryWrapper<Depart> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Depart::getId, departId);
        Depart depart = super.getOne(wrapper, false);
        if (depart != null) {
            cacheUtils.set(getCacheKeyDepart(depart.getId()), depart, 10, TimeUnit.MINUTES);
        }
        return depart;
    }

    private Depart getDepartByCodeInner(String code) {
        Object value = cacheUtils.get(getCacheKeyDepartCode(code), Depart.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.DEPART_NOT_FOUND);
        }
        if (value != null) {
            return (Depart) value;
        }
        QueryWrapper<Depart> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Depart::getCode, code);
        Depart depart = super.getOne(wrapper, false);
        if (depart != null) {
            cacheUtils.set(getCacheKeyDepartCode(code), depart, 10, TimeUnit.MINUTES);
        }
        return depart;
    }

    private void removeCahche(Long departId, String departCode) {
        if (departId != null && departId > 0) {
            cacheUtils.delete(getCacheKeyDepart(departId));
        }
        if (StringUtils.isNotEmpty(departCode)) {
            cacheUtils.delete(getCacheKeyDepartCode(departCode));
        }
    }

    private String getCacheKeyDepart(Long userId) {
        return "depart:" + userId;
    }

    private String getCacheKeyDepartCode(String code) {
        return "depart:code:" + code;
    }

}
