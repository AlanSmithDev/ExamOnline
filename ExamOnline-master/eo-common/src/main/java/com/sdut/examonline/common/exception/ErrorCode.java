package com.sdut.examonline.common.exception;

public enum ErrorCode {

    INNER_ERROR(500, "系统内部错误"),
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未登录"),
    TOKEN_EXPIRATION(402, "令牌已过期"),
    FORBIDDEN(403, "无权操作"),
    NOT_FOUND(404, "未找到"),

    USER_NAME_DUPLICATE(40001001, "用户名重复"),
    USER_NOT_FOUND(40401002, "用户不存在"),
    USER_PASSWORD_NOT_MATCH(40001003, "用户名或密码错误"),
    USER_BIRTHDAY_INVALID(40401004, "生日日期无效"),

    EXAM_NOT_FOUND(40402001, "考试不存在"),
    EXAM_NOT_RELEASED(40402002, "考试未发布"),
    EXAM_ALREADY_RELEASED(40402003, "考试已发布"),
    EXAM_PAPER_NEED_GENERATE(40402004, "试卷未生成"),
    EXAM_PAPER_ALREADY_GENERATED(40402005, "试卷已生成"),

    QUESTION_NOT_FOUND(40403001, "题目不存在"),
    QUESTION_DUPLICATE_FROM_SUB(40403003, "无法复制子题目"),
    QUESTION_ANSWER_NOT_FOUND(40403004, "题目预选答案不存在"),
    QUESTION_REPO_NOT_FOUND(40403005, "题库不存在"),
    QUESTION_REPO_PASSWORD_NOT_MATCH(40403006, "题库密码错误"),
    QUESTION_REPO_NEED_VERIFY(40403007, "需验证题库密码"),

    PAPER_NOT_FOUND(40404001, "试卷不存在"),
    PAPER_QUESTION_ANSWER_NOT_FOUND(40402003, "答题记录不存在"),
    PAPER_QUESTION_ANSWER_EXISTS(40402004, "答题记录已存在"),
    PAPER_JOIN_TIME_EXCEEDED(40402005, "超出开始答题时间"),
    PAPER_SUBMIT_TIME_LIMITED(40402006, "提交时间限制"),
    PAPER_END_TIME_EXCEEDED(40402007, "超出提交时间"),
    PAPER_NOT_ANSWERING(40402008, "试卷已无法作答"),
    PAPER_NOT_CORRECTING(40402009, "试卷不在批改中"),
    PAPER_SUBMITTED(40402010, "试卷已提交"),
    PAPER_NOT_CONTAINS_MANUAL_QUESTIONS(40402011, "试卷没有需手动批改的试题"),
    PAPER_NEED_CHECK_FACE(40402012, "需要人脸识别"),

    FILE_NOT_FOUND(40405001, "文件不存在"),

    DEPART_NOT_FOUND(40406001, "班级不存在"),
    DEPART_ALREADY_JOINED(40406002, "已加入此班级"),

    MESSAGE_NOT_FOUND(40407001, "消息不存在"),

    ;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
