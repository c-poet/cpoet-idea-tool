package cn.cpoet.tool.actions.file;

import cn.cpoet.tool.util.FileUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 在资源管理器中打开编码输出文件
 *
 * @author CPoet
 */
public class OpenOutputFileInExplorerAction extends OpenOutputFileAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        FileUtil.selectFile(outputFile);
    }
}
