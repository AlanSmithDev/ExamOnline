package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.List;

/**
 * @author Chen
 * @since 2022/3/17 11:27
 */
@Data
public class QuestionDetailDto extends BaseDto {

    private Long id;

    private Integer type;

    private Integer difficulty;

    private String content;

    private String voiceFile;

    private Long repoId;

    private Long creator;

    private String analysis;

    private String analysisVideo;

    private List<QuestionAnswerDto> answers;

}
