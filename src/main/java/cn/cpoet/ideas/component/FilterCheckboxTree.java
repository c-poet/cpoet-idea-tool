package cn.cpoet.ideas.component;

import cn.cpoet.ideas.model.TreeNodeInfo;
import cn.cpoet.ideas.util.TreeUtil;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author CPoet
 */
public class FilterCheckboxTree extends CheckboxTree {

    private final FilterCheckedTreeNode rootNode;
    private Set<TreePath> originTreePathExpandSet;

    public FilterCheckboxTree(CheckboxTreeCellRenderer cellRenderer, FilterCheckedTreeNode rootNode) {
        super(cellRenderer, rootNode);
        this.rootNode = rootNode;
    }

    public void applyFilter(@NotNull Predicate<CheckedTreeNode> filter) {
        FilterCheckedTreeNode filterNode = filter(rootNode, filter);
        List<TreePath> expandTreePaths = null;
        if (CollectionUtils.isNotEmpty(originTreePathExpandSet)) {
            expandTreePaths = new LinkedList<>();
            handleNodeExpand(filterNode, expandTreePaths);
        }
        ((DefaultTreeModel) treeModel).setRoot(filterNode);
        if (CollectionUtils.isNotEmpty(expandTreePaths)) {
            for (TreePath treePath : expandTreePaths) {
                expandPath(treePath);
            }
        }
    }

    protected void handleNodeExpand(FilterCheckedTreeNode node, List<TreePath> expandTreePaths) {
        if (node.isLeaf()) {
            return;
        }
        TreePath originTreePath = node.getOriginNode().getAndInitTreePath();
        if (originTreePathExpandSet.contains(originTreePath)) {
            TreePath treePath = node.getAndInitTreePath();
            expandTreePaths.add(treePath);
            Enumeration<TreeNode> childrenIt = node.children();
            while (childrenIt.hasMoreElements()) {
                handleNodeExpand((FilterCheckedTreeNode) childrenIt.nextElement(), expandTreePaths);
            }
        }
    }

    protected FilterCheckedTreeNode filter(FilterCheckedTreeNode node, Predicate<CheckedTreeNode> filter) {
        if (node.isLeaf()) {
            if (filter.test(node)) {
                return cloneNode(node);
            } else {
                return null;
            }
        }
        regOriginNodeExpand(node);
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

    protected void regOriginNodeExpand(FilterCheckedTreeNode node) {
        node.getAndInitTreePath();
        if (isExpanded(node.getTreePath())) {
            if (originTreePathExpandSet == null) {
                originTreePathExpandSet = new LinkedHashSet<>();
            }
            originTreePathExpandSet.add(node.getTreePath());
        }
    }

    protected FilterCheckedTreeNode cloneNode(FilterCheckedTreeNode originNoe) {
        FilterCheckedTreeNode treeNode = originNoe.clone();
        treeNode.setOriginNode(originNoe);
        return treeNode;
    }

    public void removeFilter() {
        if (treeModel.getRoot() != rootNode) {
            if (CollectionUtils.isNotEmpty(originTreePathExpandSet)) {
                handleOriginNodeExpand((FilterCheckedTreeNode) treeModel.getRoot());
            }
            ((DefaultTreeModel) treeModel).setRoot(rootNode);
            if (CollectionUtils.isNotEmpty(originTreePathExpandSet)) {
                for (TreePath treePath : originTreePathExpandSet) {
                    expandPath(treePath);
                }
            }
        }
        originTreePathExpandSet = null;
    }

    protected void handleOriginNodeExpand(FilterCheckedTreeNode node) {
        if (node.isLeaf()) {
            return;
        }
        TreePath originTreePath = node.getOriginNode().getAndInitTreePath();
        if (isExpanded(node.getTreePath())) {
            originTreePathExpandSet.add(originTreePath);
        } else {
            originTreePathExpandSet.remove(originTreePath);
        }
        if (CollectionUtils.isNotEmpty(originTreePathExpandSet)) {
            Enumeration<TreeNode> children = node.children();
            while (children.hasMoreElements()) {
                handleOriginNodeExpand((FilterCheckedTreeNode) children.nextElement());
            }
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
