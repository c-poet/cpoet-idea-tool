package cn.cpoet.ideas.iu.actions.patch.component;

import cn.cpoet.ideas.ic.component.SimpleHPanel;
import cn.cpoet.ideas.ic.component.TitledPanel;
import cn.cpoet.ideas.ic.util.I18nUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 生成补丁树面板
 *
 * @author CPoet
 */
public class GenPatchTreePanel extends JBSplitter {

    private final GenPatchTree tree;

    public GenPatchTreePanel(Project project) {
        super(true);
        tree = new GenPatchTree(project);
        buildTreePanel();
        buildDescriptionPanel();
    }

    private void buildTreePanel() {
        BorderLayoutPanel treePanel = JBUI.Panels.simplePanel();

        BorderLayoutPanel toolbarBorderLayoutPanel = JBUI.Panels.simplePanel();
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Project file tree toolbar", getTreeToolbarActionGroup(), true);
        actionToolbar.setTargetComponent(toolbarBorderLayoutPanel);
        toolbarBorderLayoutPanel.addToLeft(actionToolbar.getComponent());

        SimpleHPanel simpleHPanel = new SimpleHPanel();
        simpleHPanel.add(new JBLabel("Filter:"));
        ComboBox<Object> objectComboBox = new ComboBox<>();
        objectComboBox.addItem("Project files");
        objectComboBox.addItem("Change files");
        objectComboBox.addItem("Selected files");
        objectComboBox.setFocusable(false);
        simpleHPanel.add(objectComboBox);
        toolbarBorderLayoutPanel.addToRight(simpleHPanel);

        treePanel.addToTop(toolbarBorderLayoutPanel);

        JBScrollPane treeScrollPane = new JBScrollPane(tree);
        treePanel.addToCenter(treeScrollPane);
        setFirstComponent(treePanel);
    }

    private ActionGroup getTreeToolbarActionGroup() {
        return new ActionGroup() {
            @Override
            public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
                return new AnAction[]{
                        new AnAction(null, "Expand all", AllIcons.Actions.Expandall) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                // TODO BY CPoet 执行过慢，后续优化
                                TreeUtil.expandAll(tree);
                            }
                        },
                        new AnAction(null, "Collapse all", AllIcons.Actions.Collapseall) {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                                // TODO BY CPoet 执行过慢，后续优化
                                TreeUtil.collapseAll(tree, -1);
                            }
                        }
                };
            }
        };
    }

    private void buildDescriptionPanel() {
        TitledPanel descTitledPanel = new TitledPanel(I18nUtil.t("actions.patch.GenPatchPackageAction.description.title"));
        EditorTextField patchInfoEditor = new EditorTextField();
        descTitledPanel.add(patchInfoEditor);
        setSecondComponent(descTitledPanel);
    }

    public GenPatchTree getTree() {
        return tree;
    }
}
