package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/3/30 10:55
 */
@Data
public class QuestionIntelligentDto extends BaseDto {

    private Long repoId;

    private Integer questionType;

    private Integer amount;

    private Integer difficulty;

    private Integer score;

}
