package com.github.dapeng.api.gateway.util;
import com.github.dapeng.core.helper.IPUtils;
import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;

/**
 * @author with struy.
 * Create by 2018/5/9 13:06
 * email :yq1724555319@gmail.com
 */

public class Base64Util {
    private static final Logger LOGGER = Logger.getLogger(Base64Util.class);
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 编码
     *
     * @param text
     * @return
     */
    public static String encode(String text) {
        try {
            if (null != text) {
                final byte[] textByte = text.getBytes("UTF-8");
                return ENCODER.encodeToString(textByte);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(Base64Util.class.getName() + " :: base64 encode error", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码
     *
     * @param text
     * @return
     */
    public static String decode(String text) {
        try {
            return new String(DECODER.decode(text), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(Base64Util.class.getName() + " :: base64 decode error", e);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        final String text = "apiKey@##@timestamp";
        final String encodedText = encode(text);
        System.out.println(encodedText);
        System.out.println(decode(encodedText));
        Boolean ss = IPUtils.matchIpWithMask(IPUtils.transferIp("192.168.0.102"),IPUtils.transferIp("192.168.0.1"),24);
        System.out.println(new Date());
        System.out.println(ss);
    }
}
