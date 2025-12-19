package cn.cpoet.tool.impl223;

import cn.cpoet.tool.actions.database.CopySingleColJoinActionCompatible;
import cn.cpoet.tool.actions.patch.GenPatchConfPanelCompatible;
import cn.cpoet.tool.compatible.CompatibleProvider;
import cn.cpoet.tool.compatible.CompatibleRegister;
import cn.cpoet.tool.setting.SettingComponentCompatible;
import com.intellij.openapi.util.BuildNumber;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author CPoet
 */
public class Impl223CompatibleProvider implements CompatibleProvider {
    @Override
    public @NotNull BuildNumber getBuildNumber() {
        return Objects.requireNonNull(BuildNumber.fromString("223"));
    }

    @Override
    public void register(@NotNull CompatibleRegister register) {
        register.reg(GenPatchConfPanelCompatible.class);
        register.reg(SettingComponentCompatible.class);
        register.reg(CopySingleColJoinActionCompatible.class);
    }
}
