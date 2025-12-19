package cn.cpoet.tool.compatible;

import cn.cpoet.tool.exception.ToolException;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.BuildNumber;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CPoet
 */
@Service(Service.Level.APP)
public final class CompatibleService {

    private final Map<Class<?>, Class<?>> compatibleTable = new HashMap<>();
    private final Map<Class<?>, Object> compatibleSingleTable = new HashMap<>();

    /**
     * 获取兼容服务实例
     *
     * @return 兼容服务实例
     */
    public static CompatibleService getInstance() {
        return ApplicationManager.getApplication().getService(CompatibleService.class);
    }

    public CompatibleService() {
        ExtensionPointName<CompatibleProvider> epName = ExtensionPointName.create("cn.cpoet.tool.compatibleProvider");
        // 获取版本兼容提供
        BuildNumber curBuildNumber = ApplicationInfo.getInstance().getBuild();
        epName.getExtensionsIfPointIsRegistered(ApplicationManager.getApplication())
                .stream()
                .filter(provider -> curBuildNumber.compareTo(provider.getBuildNumber()) >= 0)
                .sorted(Comparator.comparing(CompatibleProvider::getBuildNumber).reversed())
                .forEach(provider -> {
                    CompatibleRegister register = new CompatibleRegister(this, provider, compatibleTable);
                    provider.register(register);
                });
    }

    public @NotNull <T> T instance(@NotNull Class<T> clazz) {
        return instance(clazz, false);
    }

    public @NotNull <T> T instance(@NotNull Class<T> clazz, boolean isSingle) {
        if (!isSingle) {
            return instance0(clazz);
        }
        T obj = getSingleInstance(clazz);
        if (obj == null) {
            synchronized (this) {
                obj = getSingleInstance(clazz);
                if (obj == null) {
                    obj = instance0(clazz);
                    compatibleSingleTable.put(clazz, obj);
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private <T> T getSingleInstance(Class<T> clazz) {
        Object obj = compatibleSingleTable.get(clazz);
        if (obj != null) {
            return (T) obj;
        }
        for (Map.Entry<Class<?>, Object> entry : compatibleSingleTable.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (T) entry.getValue();
            }
        }
        return null;
    }

    private <T> T instance0(Class<T> clazz) {
        Class<T> compatibleClass = getCompatibleClass(clazz);
        if (compatibleClass == null) {
            throw new ToolException("No compatibility implementation class for class " + clazz.getName() + " found");
        }
        try {
            return ConstructorUtils.invokeConstructor(compatibleClass);
        } catch (Exception e) {
            throw new ToolException("Class " + compatibleClass.getName() + " instantiation failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getCompatibleClass(Class<? super T> clazz) {
        Class<?> implClass = compatibleTable.get(clazz);
        if (implClass != null) {
            return (Class<T>) implClass;
        }
        for (Map.Entry<Class<?>, Class<?>> entry : compatibleTable.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (Class<T>) entry.getValue();
            }
        }
        return null;
    }
}
