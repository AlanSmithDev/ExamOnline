package com.sdut.examonline.file.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "文件前端展示对象", description = "文件前端展示对象")
public class FileVo extends BaseVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "后缀名")
    private String ext;

    @ApiModelProperty(value = "大小")
    private Long size;

    @ApiModelProperty(value = "上传者")
    private Long uploader;

    @ApiModelProperty(value = "文件全名")
    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

}
