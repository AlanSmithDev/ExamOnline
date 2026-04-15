package com.sdut.examonline.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AesUtils {

    /**
     * 密钥
     */
    private static final String KEY = "67a2c2fccde43eac";

    /**
     * 偏移量
     */
    public static final String IV = "2a3156ec092dcb8d";

    private static final String CBC = "AES/CBC/PKCS5Padding";

    private static final String AES = "AES";

    /**
     * 加密
     *
     * @param input 待加密内容
     * @return 加密数据
     */
    public static String encrypt(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), AES);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CBC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ignored) {
            return null;
        }
    }


    /**
     * 解密
     *
     * @param input 待解密内容
     * @return 解密数据
     */
    public static String decrypt(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), AES);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CBC);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(Base64.decodeBase64(input));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return null;
        }
    }

}
