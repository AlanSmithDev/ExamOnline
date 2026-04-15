package com.sdut.examonline.file.controller;

import com.sdut.examonline.common.constant.AliOssConstants;
import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.file.service.FileService;
import com.sdut.examonline.file.vo.FileVo;
import com.sdut.examonline.file.vo.WangEditorVo;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Slf4j
@Api("文件管理")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    FileService fileService;

    @AccessLogIgnored
    @LogOperation(msg = "文件上传", type = OperationType.CREATE)
    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    ResponseVo<FileVo> upload(@RequestParam("file") MultipartFile multipartFile) {
        return ResponseVo.success(fileService.upload(multipartFile));
    }

    @AccessLogIgnored
    @LogOperation(msg = "文件上传", type = OperationType.CREATE)
    @ApiOperation(value = "文件上传(富文本)")
    @RequestMapping(value = "/uploadEditor", method = RequestMethod.POST)
    WangEditorVo uploadEditor(@RequestParam("file") MultipartFile multipartFile) {
        WangEditorVo editorVo = new WangEditorVo();
        try {
            FileVo fileVo = fileService.upload(multipartFile);
            editorVo.setErrno(0);
            editorVo.setMessage("上传成功");
            editorVo.setData(Collections.singletonList(AliOssConstants.DOWNLOAD_URL + '/' + fileVo.getFullName()));
        } catch (Exception ignored) {
            editorVo.setErrno(1);
            editorVo.setMessage("上传失败");
        }
        return editorVo;
    }

    @LogOperation(msg = "删除文件", type = OperationType.DELETE)
    @ApiOperation(value = "删除文件")
    @RequestMapping(value = "/delete/{fileId}", method = RequestMethod.GET)
    ResponseVo<Void> delete(@PathVariable("fileId") Long fileId) {
        fileService.delete(fileId);
        return ResponseVo.success();
    }

}
