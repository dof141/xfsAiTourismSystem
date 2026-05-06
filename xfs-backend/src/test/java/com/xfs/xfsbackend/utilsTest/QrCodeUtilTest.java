package com.xfs.xfsbackend.utilsTest;

import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.junit.jupiter.api.Test;

public class QrCodeUtilTest {
//    private QrCodeUtil qrCodeUtil = new QrCodeUtil();
    @Test
    public void generateBase64QrCodeTest(){
        String test = QrCodeUtil.generateBase64QrCode("test");
        System.out.println(test);
    }
}
