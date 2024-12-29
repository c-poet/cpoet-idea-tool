package cn.cpoet.ideas.ic.util;

import cn.cpoet.ideas.ic.model.TreeNodeInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.Function;

/**
 * 树形节点处理工具
 *
 * @author CPoet
 */
public abstract class TreeUtil {
    private TreeUtil() {
    }

    public static Module findModule(TreeNodeInfo nodeInfo) {
        while (nodeInfo != null && !(nodeInfo.getObject() instanceof Module)) {
            nodeInfo = nodeInfo.getParent();
        }
        return nodeInfo == null ? null : (Module) nodeInfo.getObject();
    }

    public static <T extends DefaultMutableTreeNode> T buildWithProject(Project project, Function<Object, T> func) {
        T projectNode = func.apply(project);
        projectNode.setUserObject(new TreeNodeInfo(project.getName(), project));
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            T moduleNode = buildWithModule(module, func);
            if (moduleNode != null) {
                addTreeNodeChild(projectNode, moduleNode);
            }
        }
        return projectNode;
    }

    public static <T extends DefaultMutableTreeNode> T buildWithModule(Module module, Function<Object, T> func) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        if (sourceRoots.length == 0) {
            return null;
        }
        T moduleNode = func.apply(module);
        moduleNode.setUserObject(new TreeNodeInfo(module.getName(), module));
        for (VirtualFile sourceRoot : sourceRoots) {
            T fileNode = buildWithFile(sourceRoot, func);
            if (fileNode != null) {
                addTreeNodeChild(moduleNode, fileNode);
            }
        }
        if (moduleNode.getChildCount() == 0) {
            return null;
        }
        return moduleNode;
    }

    @SuppressWarnings("all")
    public static <T extends DefaultMutableTreeNode> T buildWithFile(VirtualFile file, Function<Object, T> func) {
        // 文件直接返回节点
        if (!file.isDirectory()) {
            T fileNode = func.apply(file);
            fileNode.setUserObject(new TreeNodeInfo(file.getName(), file));
            fileNode.setAllowsChildren(false);
            return fileNode;
        }
        // 目录判断是否存在子节点
        VirtualFile[] children = file.getChildren();
        if (children.length == 0) {
            return null;
        }
        T fileNode = func.apply(file);
        fileNode.setUserObject(new TreeNodeInfo(file.getName(), file));
        for (VirtualFile child : children) {
            T childNode = buildWithFile(child, func);
            if (childNode != null) {
                addTreeNodeChild(fileNode, childNode);
            }
        }
        if (fileNode.getChildCount() == 0) {
            return null;
        }
        if (fileNode.getChildCount() == 1) {
            T childNode = (T) fileNode.getChildAt(0);
            TreeNodeInfo nodeInfo = (TreeNodeInfo) childNode.getUserObject();
            if (((VirtualFile) nodeInfo.getObject()).isDirectory()) {
                nodeInfo.setName(file.getName() + "/" + nodeInfo.getName());
                return childNode;
            }
        }
        return fileNode;
    }

    private static void addTreeNodeChild(DefaultMutableTreeNode patent, DefaultMutableTreeNode child) {
        Object parentObj = patent.getUserObject();
        Object childObj = child.getUserObject();
        if (parentObj instanceof TreeNodeInfo && childObj instanceof TreeNodeInfo) {
            ((TreeNodeInfo) childObj).setParent((TreeNodeInfo) parentObj);
        }
        patent.add(child);
    }
}
