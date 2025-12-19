package cn.cpoet.tool.toolwin;

import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

/**
 * 内容工厂
 *
 * @author CPoet
 */
public interface ToolWinContentFactory {

    /**
     * 创建内容
     *
     * @param project 项目
     * @return 内容
     */
    @NotNull Content createContent(@NotNull Project project);
}
