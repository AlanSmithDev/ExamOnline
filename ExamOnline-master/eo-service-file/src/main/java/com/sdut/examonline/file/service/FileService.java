package com.sdut.examonline.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.file.entity.File;
import com.sdut.examonline.file.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends IService<File> {

    /**
     * 文件上传
     *
     * @param multipartFile 文件
     */
    FileVo upload(MultipartFile multipartFile);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     */
    void delete(Long fileId);
}
