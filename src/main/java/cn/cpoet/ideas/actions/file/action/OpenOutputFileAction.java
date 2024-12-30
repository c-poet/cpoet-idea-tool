package cn.cpoet.ideas.actions.file.action;

import cn.cpoet.ideas.util.ModuleUtil;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * 打开输出文件
 *
 * @author CPoet
 */
public class OpenOutputFileAction extends AnAction {

    protected VirtualFile sourceFile;
    protected VirtualFile outputFile;

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 重置文件信息
        resetFileInfo();
        // 获取当前操作的文件
        VirtualFile selectFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        // 获取当前操作文件所在的模块
        Module module = e.getData(LangDataKeys.MODULE);
        if (module != null) {
            doUpdate(module, selectFile);
        }
        // 更新操作的状态
        updateActionStatus(e);
    }

    protected void doUpdate(Module module, VirtualFile sourceFile) {
        VirtualFile outputFile = ModuleUtil.getOutputFile(module, sourceFile);
        if (outputFile != null) {
            this.sourceFile = sourceFile;
            this.outputFile = outputFile;
        }
    }

    protected void resetFileInfo() {
        sourceFile = null;
        outputFile = null;
    }

    protected void updateActionStatus(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        if (sourceFile != null) {
            presentation.setEnabled(outputFile != null);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Module module = e.getRequiredData(LangDataKeys.MODULE);
        OpenFileAction.openFile(outputFile, module.getProject());
    }
}
