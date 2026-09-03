package com.sdut.examonline.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.AliOssUtils;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.file.entity.File;
import com.sdut.examonline.file.mapper.FileMapper;
import com.sdut.examonline.file.service.FileService;
import com.sdut.examonline.file.vo.FileVo;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Override
    public FileVo upload(MultipartFile multipartFile) {
        File fileEntity = new File();
        fileEntity.setId(SnowFlakeIdWorker.nextId());
        if (StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            fileEntity.setExt(StringUtils.EMPTY);
        } else {
            fileEntity.setExt(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1));
        }
        fileEntity.setSize(multipartFile.getSize());
        fileEntity.setUploader(JwtUtils.getUserId());
        fileEntity.setCreateTime(new Date());

        super.save(fileEntity);

        try {
            AliOssUtils.uploadFile(getFullName(fileEntity), multipartFile.getInputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }

        FileVo fileVo = new FileVo();
        MapperUtils.map(fileEntity, fileVo);
        fileVo.setFullName(fileVo.getId() + (StringUtils.isEmpty(fileVo.getExt()) ? "" : "." + fileVo.getExt()));
        return fileVo;
    }

    @Override
    public void delete(Long fileId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getId, fileId);

        File fileEntity = super.getOne(wrapper, false);

        Assert.notNull(fileEntity, ErrorCode.FILE_NOT_FOUND);

        super.remove(wrapper);

        try {
            AliOssUtils.deleteFile(getFullName(fileEntity));
        } catch (Exception ignored) {

        }
    }

    private String getFullName(File fileEntity) {
        return fileEntity.getId() + (StringUtils.isEmpty(fileEntity.getExt()) ? "" : "." + fileEntity.getExt());
    }
}
