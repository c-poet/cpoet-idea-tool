package cn.cpoet.tool.actions.patch.action;

import cn.cpoet.tool.actions.patch.component.GenPatchVcsPanel;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import org.jetbrains.annotations.NotNull;

/**
 * 生成补丁包（VCS）
 *
 * @author CPoet
 */
public class GenPatchPackageVcsAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 判断当前是否存在VCS
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
        if (!vcsManager.hasActiveVcss()) {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        GenPatchVcsPanel genPatchVcsPanel = new GenPatchVcsPanel(project);
        dialogBuilder.setTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.title"));
        dialogBuilder.setCenterPanel(genPatchVcsPanel);
        dialogBuilder.addOkAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.generate"));
        dialogBuilder.addCancelAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.cancel"));
        dialogBuilder.showNotModal();
    }
}
