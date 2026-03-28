package cn.cpoet.tool.util;

import java.nio.charset.Charset;
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
        return encode4str(text, StandardCharsets.UTF_8);
    }

    public static String encode4str(String text, Charset charset) {
        return encode(text.getBytes(charset));
    }

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decode2str(String text) {
        return decode2str(text, StandardCharsets.UTF_8);
    }

    public static String decode2str(String text, Charset charset) {
        return new String(decode(text), charset);
    }

    public static byte[] decode(String text) {
        return Base64.getDecoder().decode(text);
    }
}
