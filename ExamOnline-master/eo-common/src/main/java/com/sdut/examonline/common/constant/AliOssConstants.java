package com.sdut.examonline.common.constant;

/**
 * @author Chen
 * @since 2022/3/20 17:26
 */
public class AliOssConstants {

    public static final String ENDPOINT = "oss-accelerate.aliyuncs.com";

    // TODO: 请替换为您自己的阿里云 AccessKey
    // 推荐使用环境变量: System.getenv("ALIYUN_ACCESS_KEY_ID")
    public static final String ACCESS_KEY_ID = "YOUR_ACCESS_KEY_ID";

    // TODO: 请替换为您自己的阿里云 AccessKey Secret
    // 推荐使用环境变量: System.getenv("ALIYUN_ACCESS_KEY_SECRET")
    public static final String ACCESS_KEY_SECRET = "YOUR_ACCESS_KEY_SECRET";

    public static final String BUCKET_NAME = "examonline-bucket";

    public static final String DOWNLOAD_URL = "https://" + BUCKET_NAME + "." + ENDPOINT;

}
