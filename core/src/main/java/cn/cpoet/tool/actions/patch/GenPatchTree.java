package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.component.FilterCheckboxTree;
import cn.cpoet.tool.component.FilterCheckedTreeNode;
import cn.cpoet.tool.model.TreeNodeInfo;
import cn.cpoet.tool.util.TreeUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ColoredTreeCellRenderer;

import javax.swing.*;
import java.util.Set;

/**
 * 生成补丁树形
 *
 * @author CPoet
 */
public class GenPatchTree extends FilterCheckboxTree {

    public GenPatchTree(Project project, Set<VirtualFile> selectedFiles) {
        super(new GenPatchPackageTreeCellRenderer(), TreeUtil.buildWithProject(project, (obj) -> {
            FilterCheckedTreeNode checkedTreeNode = new FilterCheckedTreeNode();
            VirtualFile file = null;
            if (obj instanceof Project) {
                file = ((Project) obj).getProjectFile();
            } else if (obj instanceof Module) {
                file = ((Module) obj).getModuleFile();
            } else if (obj instanceof VirtualFile) {
                file = (VirtualFile) obj;
            }
            checkedTreeNode.setChecked(file != null && selectedFiles.contains(file));
            return checkedTreeNode;
        }));
    }

    private static class GenPatchPackageTreeCellRenderer extends CheckboxTreeCellRenderer {
        @Override
        public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) ((CheckedTreeNode) value).getUserObject();
            ColoredTreeCellRenderer textRenderer = getTextRenderer();
            if (nodeInfo.getObject() instanceof Module) {
                textRenderer.setIcon(AllIcons.Nodes.Module);
            } else if (nodeInfo.getObject() instanceof VirtualFile) {
                if (((VirtualFile) nodeInfo.getObject()).isDirectory()) {
                    textRenderer.setIcon(AllIcons.Nodes.Folder);
                }
            }
            textRenderer.append(nodeInfo.getName());
        }
    }
}
