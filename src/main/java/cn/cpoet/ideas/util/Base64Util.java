package cn.cpoet.ideas.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * base64工具
 *
 * @author CPoet
 */
public abstract class Base64Util {
    private Base64Util() {
    }

    public static String encode4str(String text) {
        return encode(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decode2str(String text) {
        return new String(decode(text));
    }

    public static byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }
}
