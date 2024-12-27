package cn.cpoet.ideas.ic.setting;

import cn.cpoet.ideas.ic.constant.IdeasConst;
import cn.cpoet.ideas.ic.constant.LanguageEnum;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * 插件配置
 *
 * @author CPoet
 */
@Service(Service.Level.APP)
@State(name = "cn.cpoet.ideas.ic.setting.IdeasSetting", storages = @Storage(IdeasConst.SETTING_FILE_NAME))
public final class IdeasSetting implements PersistentStateComponent<IdeasSetting.State> {

    public static class State {
        /** 配置的语言 */
        public String language = LanguageEnum.ZH.getCode();
    }

    private State state = new State();

    public static IdeasSetting getInstance() {
        return ApplicationManager.getApplication().getService(IdeasSetting.class);
    }

    @NotNull
    @Override
    public IdeasSetting.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
