package cn.cpoet.tool.actions.patch.action;

import cn.cpoet.tool.actions.patch.component.GenPatchPanel;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;

/**
 * 生成补丁包（适用于增量发包的情况）
 *
 * @author CPoet
 */
public class GenPatchPackageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        GenPatchPanel packagePanel = new GenPatchPanel(project, dialogBuilder.getDialogWrapper());
        dialogBuilder.setTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.title"));
        dialogBuilder.setCenterPanel(packagePanel);
        dialogBuilder.addAction(packagePanel.getPreviewAction());
        dialogBuilder.addOkAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.generate"));
        dialogBuilder.setOkOperation(packagePanel::generate);
        dialogBuilder.addCancelAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.cancel"));
        dialogBuilder.showNotModal();
    }
}
