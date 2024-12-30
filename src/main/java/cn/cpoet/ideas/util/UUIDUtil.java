package cn.cpoet.ideas.util;

import java.util.UUID;

/**
 * UUID工具
 *
 * @author CPoet
 */
public abstract class UUIDUtil {
    private UUIDUtil() {
    }

    public static String uuid32() {
        return uuid().replace("-", "");
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
