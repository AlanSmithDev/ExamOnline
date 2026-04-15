package com.sdut.examonline.user.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * @author Chen
 * @since 2022/3/17 19:50
 */
public class QrCodeUtils {

    /**
     * 创建二维码并以base64编码返回
     *
     * @param content 二维码内容
     */
    public static String createQrCodeBase64(String content) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

}
