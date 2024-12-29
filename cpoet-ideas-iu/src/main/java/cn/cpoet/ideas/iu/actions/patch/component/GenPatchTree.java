package cn.cpoet.ideas.iu.actions.patch.component;

import cn.cpoet.ideas.ic.component.FilterCheckboxTree;
import cn.cpoet.ideas.ic.component.FilterCheckedTreeNode;
import cn.cpoet.ideas.ic.model.TreeNodeInfo;
import cn.cpoet.ideas.ic.util.TreeUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ColoredTreeCellRenderer;

import javax.swing.*;

/**
 * 生成补丁树形
 *
 * @author CPoet
 */
public class GenPatchTree extends FilterCheckboxTree {

    public GenPatchTree(Project project) {
        super(new GenPatchPackageTreeCellRenderer(), TreeUtil.buildWithProject(project, (obj) -> {
            FilterCheckedTreeNode checkedTreeNode = new FilterCheckedTreeNode();
            checkedTreeNode.setChecked(false);
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
