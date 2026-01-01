package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.List;

@Data
public class DepartMembersDto extends BaseDto {

    private Long departId;

    private String deptName;

    private List<Long> members;

}
