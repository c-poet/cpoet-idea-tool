package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 生成补丁包（适用于增量发包的情况）
 *
 * @author CPoet
 */
public class GenPatchPackageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Object[] selectedItems = getSelectedItems(e);
        DialogBuilder dialogBuilder = new DialogBuilder(project);
        GenPatchPanel packagePanel = new GenPatchPanel(project, selectedItems, dialogBuilder.getDialogWrapper());
        dialogBuilder.setTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.title"));
        dialogBuilder.setCenterPanel(packagePanel);
        dialogBuilder.addAction(packagePanel.getPreviewAction());
        dialogBuilder.addOkAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.generate"));
        dialogBuilder.setOkOperation(packagePanel::generate);
        dialogBuilder.addCancelAction().setText(I18nUtil.t("actions.patch.GenPatchPackageAction.cancel"));
        dialogBuilder.showNotModal();
    }

    private Object[] getSelectedItems(AnActionEvent e) {
        Object[] items = e.getData(LangDataKeys.SELECTED_ITEMS);
        Change[] changes = e.getData(VcsDataKeys.SELECTED_CHANGES);
        if (items == null && changes == null) {
            return null;
        }
        if (items != null && changes != null) {
            List<Object> selectedItems = new ArrayList<>(items.length + changes.length);
            Collections.addAll(selectedItems, items);
            for (Change change : changes) {
                selectedItems.add(change.getVirtualFile());
            }
            return selectedItems.toArray();
        }
        if (items != null) {
            return items;
        }
        VirtualFile[] files = new VirtualFile[changes.length];
        for (int i = 0; i < changes.length; ++i) {
            files[i] = changes[i].getVirtualFile();
        }
        return files;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
