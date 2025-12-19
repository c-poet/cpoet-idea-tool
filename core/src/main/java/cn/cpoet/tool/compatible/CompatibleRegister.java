package cn.cpoet.tool.compatible;

import cn.cpoet.tool.exception.ToolException;
import cn.cpoet.tool.util.ClassUtil;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author CPoet
 */
public class CompatibleRegister {

    private final CompatibleService service;
    private final CompatibleProvider provider;
    private final Map<Class<?>, Class<?>> compatibleTable;

    public CompatibleRegister(CompatibleService service, CompatibleProvider provider, Map<Class<?>, Class<?>> compatibleTable) {
        this.service = service;
        this.provider = provider;
        this.compatibleTable = compatibleTable;
    }

    private String getBasePackageName() {
        String name = provider.getName();
        if (StringUtils.isBlank(name)) {
            return provider.getClass().getPackageName();
        }
        return provider.getClass().getPackageName();
    }

    private String getImplName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name + "Impl";
    }

    public void reg(@NotNull Class<?> clazz) {
        Class<?> compatibleClass = service.getCompatibleClass(clazz);
        if (compatibleClass != null) {
            return;
        }
        String basePackageName = getBasePackageName();
        String implName = getImplName(clazz);
        Class<?> targetClass = ClassUtil.tryGetClass(basePackageName + "." + implName);
        if (targetClass == null) {
            List<String> packagePaths = Arrays.asList(clazz.getPackageName().split("\\."));
            int i = packagePaths.size();
            while (targetClass == null && --i >= 0) {
                targetClass = ClassUtil.tryGetClass(basePackageName + "." + String.join(".", packagePaths.subList(i, packagePaths.size())) + "." + implName);
            }
        }
        if (targetClass == null) {
            throw new ToolException("Class " + clazz.getName() + " compatibility implementation class loading failed");
        }
        compatibleTable.put(clazz, targetClass);
    }

    public void reg(@NotNull Class<?> clazz, @NotNull String implName) {
        Class<?> compatibleClass = service.getCompatibleClass(clazz);
        if (compatibleClass != null) {
            return;
        }
        try {
            Class<?> targetClass = ClassUtils.getClass(implName);
            compatibleTable.put(clazz, targetClass);
        } catch (Exception e) {
            throw new ToolException("Compatible implementation of class " + clazz.getName() + " cannot be loaded class " + implName);
        }
    }
}
