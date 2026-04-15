package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/3/17 10:58
 */
@Data
public class UserDto extends BaseDto {

    private Long id;

    private String userName;

    private String realName;

    private String role;

    private String avatar;

    private String birthday;

    private Integer gender;

}
