package com.sdut.examonline.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sdut.examonline.common.constant.AliOssConstants;

import java.io.InputStream;

/**
 * @author Chen
 * @since 2022/3/20 17:29
 */
public class AliOssUtils {

    private static final OSS CLIENT;

    static {
        CLIENT = new OSSClientBuilder().build(AliOssConstants.ENDPOINT, AliOssConstants.ACCESS_KEY_ID, AliOssConstants.ACCESS_KEY_SECRET);
    }

    public static void uploadFile(String fileName, InputStream inputStream) {
        CLIENT.putObject(AliOssConstants.BUCKET_NAME, fileName, inputStream);
    }

    public static boolean isFileExists(String fileName) {
        return CLIENT.doesObjectExist(AliOssConstants.BUCKET_NAME, fileName);
    }

    public static void deleteFile(String fileName) {
        CLIENT.deleteObject(AliOssConstants.BUCKET_NAME, fileName);
    }
}
