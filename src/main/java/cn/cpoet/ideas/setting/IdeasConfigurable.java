package cn.cpoet.ideas.setting;

import cn.cpoet.ideas.constant.LanguageEnum;
import cn.cpoet.ideas.i18n.I18n;
import com.intellij.openapi.options.Configurable;
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
    public String getDisplayName() {
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
        // 判断是否需要重新加载语言
        String oldLanguage = state.language;
        state.language = settingComponent.getLanguage().getCode();
        if (!oldLanguage.equals(state.language)) {
            I18n.updateLocale();
        }
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
