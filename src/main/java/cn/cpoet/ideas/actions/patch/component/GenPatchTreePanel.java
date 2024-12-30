package cn.cpoet.ideas.actions.patch.component;

import cn.cpoet.ideas.actions.patch.constant.GenPatchTreeFilterTypeEnum;
import cn.cpoet.ideas.actions.patch.setting.GenPatchSetting;
import cn.cpoet.ideas.component.CustomComboBox;
import cn.cpoet.ideas.component.SimpleHPanel;
import cn.cpoet.ideas.component.TitledPanel;
import cn.cpoet.ideas.i18n.I18n;
import cn.cpoet.ideas.model.TreeNodeInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ItemEvent;

/**
 * 生成补丁树面板
 *
 * @author CPoet
 */
public class GenPatchTreePanel extends JBSplitter {

    private final Project project;
    private final GenPatchTree tree;

    public GenPatchTreePanel(Project project) {
        super(true);
        this.project = project;
        tree = new GenPatchTree(project);
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        buildTreePanel(setting);
        buildDescriptionPanel();
    }

    private void buildTreePanel(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        BorderLayoutPanel treePanel = JBUI.Panels.simplePanel();
        BorderLayoutPanel toolbarBorderLayoutPanel = JBUI.Panels.simplePanel();
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Project file tree toolbar", getTreeToolbarActionGroup(), true);
        actionToolbar.setTargetComponent(toolbarBorderLayoutPanel);
        toolbarBorderLayoutPanel.addToLeft(actionToolbar.getComponent());

        SimpleHPanel treeFilterPanel = new SimpleHPanel();
        treeFilterPanel.add(new JBLabel(I18n.t("actions.patch.GenPatchPackageAction.treeFilterType.label")));
        CustomComboBox<GenPatchTreeFilterTypeEnum> treeFilterTypeComboBox = new CustomComboBox<>();
        for (GenPatchTreeFilterTypeEnum item : GenPatchTreeFilterTypeEnum.values()) {
            treeFilterTypeComboBox.addItem(item);
        }
        treeFilterTypeComboBox.setSelectedItem(state.treeFilterType);
        treeFilterTypeComboBox.customText(GenPatchTreeFilterTypeEnum::getTitle);
        treeFilterTypeComboBox.setFocusable(false);
        treeFilterTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                doFilterTree((GenPatchTreeFilterTypeEnum) e.getItem());
                setting.getState().treeFilterType = ((GenPatchTreeFilterTypeEnum) e.getItem()).getCode();
            }
        });
        treeFilterPanel.add(treeFilterTypeComboBox);
        toolbarBorderLayoutPanel.addToRight(treeFilterPanel);
        treePanel.addToTop(toolbarBorderLayoutPanel);
        JBScrollPane treeScrollPane = new JBScrollPane(tree);
        treePanel.addToCenter(treeScrollPane);
        setFirstComponent(treePanel);
    }

    private void doFilterTree(GenPatchTreeFilterTypeEnum filterType) {
        switch (filterType) {
            case EDITOR:
                doFilterTreeEditor();
                break;
            case CHANGE:
                doFilterTreeChange();
                break;
            case SELECTED:
                doFilterTreeSelected();
                break;
            case PROJECT:
            default:
                tree.removeFilter();
        }
    }

    private void doFilterTreeEditor() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        tree.applyFilter((node -> {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) node.getUserObject();
            if (nodeInfo.getObject() instanceof VirtualFile) {
                return fileEditorManager.isFileOpen((VirtualFile) nodeInfo.getObject());
            }
            return false;
        }));
    }

    private void doFilterTreeChange() {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        tree.applyFilter(node -> {
            TreeNodeInfo nodeInfo = (TreeNodeInfo) node.getUserObject();
            if (nodeInfo.getObject() instanceof VirtualFile) {
                return fileDocumentManager.isFileModified((VirtualFile) nodeInfo.getObject());
            }
            return false;
        });
    }

    private void doFilterTreeSelected() {
        tree.applyFilter(CheckedTreeNode::isChecked);
    }

    private ActionGroup getTreeToolbarActionGroup() {
        return new ActionGroup() {
            @Override
            public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
                return new AnAction[]{
                        new AnAction("Expand All", null, AllIcons.Actions.Expandall) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                TreeUtil.expandAll(tree);
                            }
                        },
                        new AnAction("Collapse All", null, AllIcons.Actions.Collapseall) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                TreeUtil.collapseAll(tree, -1);
                            }
                        }
                };
            }
        };
    }

    private void buildDescriptionPanel() {
        TitledPanel descTitledPanel = new TitledPanel(I18n.t("actions.patch.GenPatchPackageAction.description.title"));
        EditorTextField patchInfoEditor = new EditorTextField();
        descTitledPanel.add(patchInfoEditor);
        setSecondComponent(descTitledPanel);
    }

    public GenPatchTree getTree() {
        return tree;
    }
}
