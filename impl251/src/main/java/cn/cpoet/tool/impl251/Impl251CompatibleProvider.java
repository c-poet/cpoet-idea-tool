package cn.cpoet.tool.impl251;

import cn.cpoet.tool.actions.database.CopySingleColJoinActionCompatible;
import cn.cpoet.tool.compatible.CompatibleProvider;
import cn.cpoet.tool.compatible.CompatibleRegister;
import com.intellij.openapi.util.BuildNumber;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author CPoet
 */
public class Impl251CompatibleProvider implements CompatibleProvider {
    @Override
    public @NotNull BuildNumber getBuildNumber() {
        return Objects.requireNonNull(BuildNumber.fromString("251"));
    }

    @Override
    public void register(@NotNull CompatibleRegister register) {
        // register.reg(CopySingleColJoinActionCompatible.class);
    }
}
