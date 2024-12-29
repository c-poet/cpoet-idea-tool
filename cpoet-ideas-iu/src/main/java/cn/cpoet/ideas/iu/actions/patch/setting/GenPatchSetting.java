package cn.cpoet.ideas.iu.actions.patch.setting;

import cn.cpoet.ideas.ic.constant.IdeasConst;
import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchTreeFilterTypeEnum;
import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchProjectTypeEnum;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 生成补丁配置
 *
 * @author CPoet
 */
@Service(Service.Level.PROJECT)
@State(name = "cn.cpoet.ideas.ic.actions.patch.setting.GenPatchSetting", storages = @Storage(IdeasConst.SETTING_FILE_NAME))
public final class GenPatchSetting implements PersistentStateComponent<GenPatchSetting.State> {

    public static class State {
        /** 面板宽度 */
        public int width = 720;

        /** 面板高度 */
        public int height = 400;

        /** 文件树过滤类型 */
        public String treeFilterType = GenPatchTreeFilterTypeEnum.PROJECT.getCode();

        /** 输出目录 */
        public String outputFolder;

        /** 包含路径 */
        public boolean includePath;

        /** 项目类型 */
        public String projectType = GenPatchProjectTypeEnum.NONE.getCode();

        /** 编译类型 */
        public String buildType = GenPatchBuildTypeEnum.DEFAULT.getCode();

        /** 生成后清理文件 */
        public boolean cleanBuildFile;

        /** 生成后打开输出的目录 */
        public boolean openOutputFolder = true;
    }

    private State state = new State();

    public static GenPatchSetting getInstance(Project project) {
        return project.getService(GenPatchSetting.class);
    }

    @NotNull
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
