package com.xfs.xfsbackend.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 二维码生成工具类。
 * 将订单号或核销码转换成前端可直接展示的 Base64 PNG 图片。
 */
public class QrCodeUtil {

    /**
     * 根据文本内容生成二维码图片。
     * 返回值包含 data:image/png;base64 前缀，前端可直接放入 image/img 的 src 属性。
     */
    public static String generateBase64QrCode(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // 生成 250x250 像素的二维码矩阵
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // 将图片字节转为 Base64 并加上前缀，前端可以直接放到 <img src="..."> 里
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64Image;
        } catch (Exception e) {
            throw new RuntimeException("二维码生成失败: " + e.getMessage(), e);
        }
    }
}
