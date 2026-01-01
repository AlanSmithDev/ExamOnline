package com.sdut.examonline.file.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "富文本编辑器前端展示对象", description = "富文本编辑器前端展示对象")
public class WangEditorVo extends BaseVo {

    @ApiModelProperty(value = "响应代码")
    private Integer errno;

    @ApiModelProperty(value = "响应消息")
    private String message;

    @ApiModelProperty(value = "图片地址")
    private List<String> data;

    @Data
    @ApiModel(value = "图片前端展示对象", description = "图片前端展示对象")
    public static class ImageDetailVo extends BaseVo {

        @ApiModelProperty(value = "图片地址")
        private String url;

        @ApiModelProperty(value = "图片文字说明")
        private String alt;

        @ApiModelProperty(value = "跳转链接")
        private String href;

    }

}
