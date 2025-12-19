package cn.cpoet.tool.setting;

import cn.cpoet.tool.constant.LanguageEnum;
import cn.cpoet.tool.util.I18nUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 插件配置
 *
 * @author CPoet
 */
public class Configurable implements com.intellij.openapi.options.Configurable {

    private SettingComponent settingComponent;

    @Override
    public String getDisplayName() {
        return "CPoet Tool";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingComponent = new SettingComponent();
        return settingComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        Setting.State state = Setting.getInstance().getState();
        return !settingComponent.getLanguage().getCode().equals(state.language)
                || !Objects.equals(settingComponent.getPatchAssistant2JPath(), state.patchAssistant2JPath);
    }

    @Override
    public void apply() {
        Setting.State state = Setting.getInstance().getState();
        // 判断是否需要重新加载语言
        String oldLanguage = state.language;
        state.language = settingComponent.getLanguage().getCode();
        if (!oldLanguage.equals(state.language)) {
            I18nUtil.updateLocale();
        }
        state.patchAssistant2JPath = settingComponent.getPatchAssistant2JPath();
    }

    @Override
    public void reset() {
        Setting.State state = Setting.getInstance().getState();
        settingComponent.setLanguage(LanguageEnum.ofCode(state.language));
        settingComponent.setPatchAssistant2JPath(state.patchAssistant2JPath);
    }

    @Override
    public void disposeUIResources() {
        settingComponent = null;
    }
}
