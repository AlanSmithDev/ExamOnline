package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.List;

/**
 * @author Chen
 * @since 2022/3/30 14:06
 */
@Data
public class PaperSettingDto extends BaseDto {

    private String title;

    private Boolean timeLimit;

    private Boolean faceCheck;

    private Integer joinLimit;

    private Integer submitLimit;

    private Long startTime;

    private Long endTime;

    private List<Long> participants;

}
