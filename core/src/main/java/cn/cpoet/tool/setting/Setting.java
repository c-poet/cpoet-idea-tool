package cn.cpoet.tool.setting;

import cn.cpoet.tool.constant.CommonConst;
import cn.cpoet.tool.constant.LanguageEnum;
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
@State(name = "cn.cpoet.tool.setting.Setting", storages = @Storage(CommonConst.SETTING_FILE_NAME))
public final class Setting implements PersistentStateComponent<Setting.State> {

    public static class State {
        /** 配置的语言 */
        public String language = LanguageEnum.ZH.getCode();

        /**
         * 替换补丁包工具所在路径
         */
        public String patchAssistant2JPath;
    }

    private State state = new State();

    public static Setting getInstance() {
        return ApplicationManager.getApplication().getService(Setting.class);
    }

    @NotNull
    @Override
    public Setting.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
