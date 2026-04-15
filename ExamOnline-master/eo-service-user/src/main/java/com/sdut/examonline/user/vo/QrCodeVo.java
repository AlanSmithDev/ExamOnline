package com.sdut.examonline.user.vo;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/3/17 19:40
 */
@Data
@ApiModel(value = "二维码前端展示对象", description = "二维码前端展示对象")
public class QrCodeVo extends BaseDto {

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "图片")
    private String image;

}
