package cn.cpoet.tool.util;

import cn.cpoet.tool.exception.ToolException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author CPoet
 */
public class SerializeUtil {
    private SerializeUtil() {
    }

    public static byte[] writeAsBytes(Object serializable) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(serializable);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ToolException("Data serialization failed", e);
        }
    }

    public static String writeAsString(Object serializable) {
        return new String(writeAsBytes(serializable));
    }

    @SuppressWarnings("unchecked")
    public static <T> T read(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new ToolException("Data deserialization failed", e);
        }
    }

    public static <T> T read(String content) {
        return read(content.getBytes(StandardCharsets.UTF_8));
    }
}
