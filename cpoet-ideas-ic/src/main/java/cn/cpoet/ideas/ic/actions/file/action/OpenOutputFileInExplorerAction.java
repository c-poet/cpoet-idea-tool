package cn.cpoet.ideas.ic.actions.file.action;

import cn.cpoet.ideas.ic.constant.OSExplorerConst;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.FilenameUtils;
import org.jdesktop.swingx.util.OS;

/**
 * 在资源管理器中打开编码输出文件
 *
 * @author CPoet
 */
public class OpenOutputFileInExplorerAction extends OpenOutputFileAction {

    private static final Logger LOG = Logger.getInstance(OpenOutputFileInExplorerAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        String filePath = FilenameUtils.separatorsToSystem(outputFile.getPath());
        if (OS.isLinux()) {
            execOpenCommand(OSExplorerConst.LINUX_GNOME, filePath);
        } else if (OS.isMacOSX()) {
            execOpenCommand(OSExplorerConst.MACOS, filePath);
        } else {
            execOpenCommand(OSExplorerConst.WINDOWS, "/e,/select," + filePath);
        }
    }

    protected void execOpenCommand(String... commandAndArgs) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commandAndArgs);
        } catch (Exception e) {
            LOG.error("The command execution error", e);
        }
    }
}
