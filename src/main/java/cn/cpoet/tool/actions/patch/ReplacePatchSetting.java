package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.constant.CommonConst;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
@Service(Service.Level.PROJECT)
@State(name = "cn.cpoet.tool.actions.patch.ReplacePatchSetting", storages = @Storage(CommonConst.SETTING_FILE_NAME))
public final class ReplacePatchSetting implements PersistentStateComponent<ReplacePatchSetting.State> {

    public static class State {
        /**
         * 面板宽度
         */
        public int width = 720;

        /**
         * 面板高度
         */
        public int height = 400;

        /**
         * 程序包所在路径
         */
        public String appPackagePath;
    }

    private State state = new State();

    public static ReplacePatchSetting getInstance(Project project) {
        return project.getService(ReplacePatchSetting.class);
    }

    @Override
    @NotNull
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
