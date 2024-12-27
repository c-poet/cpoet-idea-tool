package cn.cpoet.ideas.ic.setting;

import cn.cpoet.ideas.ic.constant.LanguageEnum;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 插件配置
 *
 * @author CPoet
 */
public class IdeasConfigurable implements Configurable {

    private IdeasSettingComponent settingComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "CPoet Ideas";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingComponent = new IdeasSettingComponent();
        return settingComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        IdeasSetting.State state = IdeasSetting.getInstance().getState();
        return !settingComponent.getLanguage().getCode().equals(state.language);
    }

    @Override
    public void apply() {
        IdeasSetting.State state = IdeasSetting.getInstance().getState();
        state.language = settingComponent.getLanguage().getCode();
    }

    @Override
    public void reset() {
        IdeasSetting.State state = IdeasSetting.getInstance().getState();
        settingComponent.setLanguage(LanguageEnum.ofCode(state.language));
    }

    @Override
    public void disposeUIResources() {
        settingComponent = null;
    }
}
