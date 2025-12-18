package cn.cpoet.tool.util;

import cn.cpoet.tool.exception.ToolException;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author CPoet
 */
public abstract class ReflectUtil {

    private ReflectUtil() {
    }

    /**
     * 调用实例方法
     *
     * @param obj  对象
     * @param name 名称
     * @param args 参数列表
     * @param <T>  返回值类型
     * @return 调用结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, String name, Object... args) {
        try {
            return (T) MethodUtils.invokeMethod(obj, name, args);
        } catch (Exception e) {
            throw new ToolException("Invoke method failed", e);
        }
    }

    /**
     * 调用静态方法
     *
     * @param clazz 类
     * @param name  方法名
     * @param args  参数列表
     * @param <T>   返回值类型
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(Class<?> clazz, String name, Object... args) {
        try {
            return (T) MethodUtils.invokeStaticMethod(clazz, name, args);
        } catch (Exception e) {
            throw new ToolException("Invoke method failed", e);
        }
    }
}
