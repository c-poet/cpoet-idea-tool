package cn.cpoet.tool.toolwin;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * 工具栏工厂
 *
 * @author CPoet
 */
public class ToolWinFactory implements ToolWindowFactory {

    private static final ExtensionPointName<ToolWinContentFactory> EP_NAME = ExtensionPointName.create("cn.cpoet.tool.toolWinContentFactory");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        EP_NAME.forEachExtensionSafe(toolWinContentFactory -> {
            Content content = toolWinContentFactory.createContent(project);
            contentManager.addContent(content);
        });
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return EP_NAME.hasAnyExtensions();
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }
}
