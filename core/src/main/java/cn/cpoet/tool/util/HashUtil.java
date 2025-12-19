package cn.cpoet.tool.util;

import cn.cpoet.tool.exception.ToolException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author CPoet
 */
public abstract class HashUtil {

    private HashUtil() {
    }

    public static MessageDigest createMd5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new ToolException("The MD5 algorithm is not supported", e);
        }
    }

    public static MessageDigest createSha1Digest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new ToolException("The SHA1 algorithm is not supported", e);
        }
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 字节数组
     * @return 转换结果
     */
    public static String toHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 计算md5值
     *
     * @param bytes 字节数组
     * @return md5值
     */
    public static String md5(byte[] bytes) {
        byte[] digest = createMd5Digest().digest(bytes);
        return toHexStr(digest);
    }

    /**
     * 计算md5值
     *
     * @param str 字符串
     * @return md5值
     */
    public static String md5(String str) {
        return md5(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算sha1值
     *
     * @param bytes 字节数组
     * @return sha1值
     */
    public static String sha1(byte[] bytes) {
        byte[] digest = createSha1Digest().digest(bytes);
        return toHexStr(digest);
    }

    /**
     * 计算sha1值
     *
     * @param str 字符串
     * @return sha1值
     */
    public static String sha1(String str) {
        return sha1(str.getBytes(StandardCharsets.UTF_8));
    }
}
