package cn.cpoet.ideas.actions.patch.action;

import cn.cpoet.ideas.actions.patch.component.GenPatchVcsPanel;
import cn.cpoet.ideas.i18n.I18n;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vcs.AbstractVcs;
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
        AbstractVcs[] allActiveVcss = vcsManager.getAllActiveVcss();
        System.out.println(allActiveVcss);
        AbstractVcs[] allSupportedVcss = vcsManager.getAllSupportedVcss();
        System.out.println(allSupportedVcss);
        if (!vcsManager.hasActiveVcss()) {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        GenPatchVcsPanel genPatchVcsPanel = new GenPatchVcsPanel(project);
        dialogBuilder.setTitle(I18n.t("actions.patch.GenPatchPackageAction.title"));
        dialogBuilder.setCenterPanel(genPatchVcsPanel);
        dialogBuilder.addOkAction().setText(I18n.t("actions.patch.GenPatchPackageAction.generate"));
        dialogBuilder.addCancelAction().setText(I18n.t("actions.patch.GenPatchPackageAction.cancel"));
        dialogBuilder.showNotModal();
    }
}
