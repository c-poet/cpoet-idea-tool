package cn.cpoet.tool.compatible;

import com.intellij.openapi.util.BuildNumber;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public interface CompatibleProvider {
    /**
     * 获取兼容版本名称
     *
     * @return 兼容版本名称
     */
    default String getName() {
        return null;
    }

    /**
     * 获取当前版本
     *
     * @return 当前版本
     */
    @NotNull
    BuildNumber getBuildNumber();

    /**
     * 注册兼容版本
     *
     * @param register 兼容版本注册器
     */
    void register(@NotNull CompatibleRegister register);
}
