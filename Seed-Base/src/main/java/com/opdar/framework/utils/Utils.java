package com.opdar.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class Utils {
    public static byte[] is2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(b)) != -1) {
            arrayBuffer.write(b, 0, len);
        }
        inputStream.close();
        arrayBuffer.close();
        return arrayBuffer.toByteArray();
    }

    public static char byte2Char(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }
}
