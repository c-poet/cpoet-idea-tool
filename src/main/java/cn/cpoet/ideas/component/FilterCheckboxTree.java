package cn.cpoet.ideas.component;

import cn.cpoet.ideas.model.TreeNodeInfo;
import cn.cpoet.ideas.util.TreeUtil;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.function.Predicate;

/**
 * @author CPoet
 */
public class FilterCheckboxTree extends CheckboxTree {

    private final FilterCheckedTreeNode rootNode;

    public FilterCheckboxTree(CheckboxTreeCellRenderer cellRenderer, FilterCheckedTreeNode rootNode) {
        super(cellRenderer, rootNode);
        this.rootNode = rootNode;
    }

    public void applyFilter(@NotNull Predicate<CheckedTreeNode> filter) {
        FilterCheckedTreeNode filterNode = filter(rootNode, filter);
        // 获取当前已经扩展的路径
        ((DefaultTreeModel) treeModel).setRoot(filterNode);
    }

    protected FilterCheckedTreeNode filter(FilterCheckedTreeNode node, Predicate<CheckedTreeNode> filter) {
        if (node.isLeaf()) {
            if (filter.test(node)) {
                return cloneNode(node);
            } else {
                return null;
            }
        }
        FilterCheckedTreeNode newNode = cloneNode(node);
        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            FilterCheckedTreeNode newChildNode = filter((FilterCheckedTreeNode) children.nextElement(), filter);
            if (newChildNode != null) {
                newNode.add(newChildNode);
            }
        }
        if (newNode.getChildCount() == 0 && !node.isRoot()) {
            return null;
        }
        return newNode;
    }

    protected FilterCheckedTreeNode cloneNode(FilterCheckedTreeNode originNoe) {
        FilterCheckedTreeNode treeNode = (FilterCheckedTreeNode) originNoe.clone();
        treeNode.setOriginNode(originNoe);
        return treeNode;
    }

    public void removeFilter() {
        if (treeModel.getRoot() != rootNode) {
            ((DefaultTreeModel) treeModel).setRoot(rootNode);
        }
    }

    @Override
    public <T> T[] getCheckedNodes(Class<? extends T> nodeType, @Nullable Tree.NodeFilter<? super T> filter) {
        return TreeUtil.getCheckedNodes(nodeType, filter, rootNode);
    }

    @Override
    protected void installSpeedSearch() {
        new TreeSpeedSearch(this, treePath -> {
            CheckedTreeNode treeNode = (CheckedTreeNode) treePath.getLastPathComponent();
            return treeNode.getUserObject() instanceof TreeNodeInfo
                    ? ((TreeNodeInfo) treeNode.getUserObject()).getName()
                    : treeNode.toString();
        }, true);
    }
}
