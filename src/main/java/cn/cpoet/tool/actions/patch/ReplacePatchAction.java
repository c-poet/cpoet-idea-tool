package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class ReplacePatchAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        openReplacePatchDialog(project, null);
    }

    public static void openReplacePatchDialog(Project project, String path) {
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        ReplacePatchPanel replacePatchPanel = new ReplacePatchPanel(project, path);
        dialogBuilder.setTitle(I18nUtil.t("actions.patch.ReplacePatchAction.title"));
        dialogBuilder.setCenterPanel(replacePatchPanel);
        dialogBuilder.setOkOperation(replacePatchPanel::handleReplace);
        dialogBuilder.show();
    }
}
