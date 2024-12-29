package cn.cpoet.ideas.iu.actions.patch.component;

import cn.cpoet.ideas.ic.i18n.I18n;
import cn.cpoet.ideas.ic.model.TreeNodeInfo;
import cn.cpoet.ideas.ic.util.ModuleUtil;
import cn.cpoet.ideas.ic.util.TreeUtil;
import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.iu.actions.patch.model.GenPatch;
import cn.cpoet.ideas.iu.actions.patch.model.GenPatchItem;
import cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ex.MessagesEx;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 补丁包生成视图
 *
 * @author CPoet
 */
public class GenPatchPanel extends JBSplitter {
    /** 项目信息 */
    private final Project project;
    /** 预览操作 */
    private Action previewAction;
    /** Dialog */
    private final DialogWrapper dialogWrapper;
    /** 选中统计 */
    private final AtomicInteger checkedCount;
    /** 文件树视图 */
    private final GenPatchTreePanel treePanel;
    /** 配置视图 */
    private final GenPatchConfPane confPanel;


    public GenPatchPanel(Project project, DataContext dataContext, DialogWrapper dialogWrapper) {
        this.project = project;
        this.dialogWrapper = dialogWrapper;
        cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting setting = cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting.getInstance(project);
        setPreferredSize(new JBDimension(setting.getState().width, setting.getState().height));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setting.getState().width = getWidth();
                setting.getState().height = getHeight();
            }
        });
        treePanel = new GenPatchTreePanel(project, dataContext);
        checkedCount = new AtomicInteger(getTreeCheckedNodes().length);
        addCheckboxTreeListener();
        setFirstComponent(treePanel);
        confPanel = new GenPatchConfPane(project);
        setSecondComponent(confPanel);
    }

    private void addCheckboxTreeListener() {
        dialogWrapper.setOKActionEnabled(checkedCount.get() > 0);
        treePanel.getTree().addCheckboxTreeListener(new CheckboxTreeListener() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                if (node.isChecked()) {
                    checkedCount.incrementAndGet();
                } else {
                    checkedCount.decrementAndGet();
                }
                dialogWrapper.setOKActionEnabled(checkedCount.get() > 0);
            }
        });
    }

    public void generate() {
        VirtualFile patchOutputPath = getPatchOutputPath();
        if (patchOutputPath == null) {
            return;
        }
        dialogWrapper.setOKActionEnabled(false);
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting setting = cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting.getInstance(project);
        GenPatchBuildTypeEnum buildTypeEnum = GenPatchBuildTypeEnum.ofCode(setting.getState().buildType);
        if (buildTypeEnum == GenPatchBuildTypeEnum.PROJECT) {
            ProjectTaskManager.getInstance(project).rebuildAllModules()
                    .onProcessed(ret -> {
                        if (!ret.hasErrors()) {
                            doGenerate(patchOutputPath, setting, checkedNodes);
                        }
                        dialogWrapper.setOKActionEnabled(true);
                    });
        } else if (buildTypeEnum == GenPatchBuildTypeEnum.MODULE) {
            Set<Module> modules = new HashSet<>();
            for (TreeNodeInfo checkedNode : checkedNodes) {
                Module module = TreeUtil.findModule(checkedNode);
                if (module != null) {
                    modules.add(module);
                }
            }
            ProjectTaskManager.getInstance(project).rebuild(modules.toArray(Module[]::new))
                    .onProcessed(ret -> {
                        if (!ret.hasErrors()) {
                            doGenerate(patchOutputPath, setting, checkedNodes);
                        }
                        dialogWrapper.setOKActionEnabled(true);
                    });
        } else if (buildTypeEnum == GenPatchBuildTypeEnum.FILE) {
            VirtualFile[] files = Arrays.stream(checkedNodes).map(nodeInfo -> (VirtualFile) nodeInfo.getObject()).toArray(VirtualFile[]::new);
            ProjectTaskManager.getInstance(project).compile(files).onProcessed(ret -> {
                if (!ret.hasErrors()) {
                    doGenerate(patchOutputPath, setting, checkedNodes);
                }
                dialogWrapper.setOKActionEnabled(true);
            });
        } else {
            doGenerate(patchOutputPath, setting, checkedNodes);
            dialogWrapper.setOKActionEnabled(true);
        }
    }

    protected VirtualFile getPatchOutputPath() {
        VirtualFile folder = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, null);
        if (folder == null) {
            MessagesEx.error(project, "Please select directory").showNow();
        }
        return folder;
    }

    protected void doGenerate(VirtualFile outputPath, cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting setting, TreeNodeInfo[] treeNodeInfos) {
        cn.cpoet.ideas.iu.actions.patch.model.GenPatch patch = new cn.cpoet.ideas.iu.actions.patch.model.GenPatch();
        patch.setOutputPath(outputPath);
        patch.getDesc().append("替换路径:");
        for (TreeNodeInfo checkedNode : treeNodeInfos) {
            Module module = TreeUtil.findModule(checkedNode);
            VirtualFile outputFile = ModuleUtil.getOutputFile(module, (VirtualFile) checkedNode.getObject());
            if (outputFile != null) {
                VirtualFile sourceFile = (VirtualFile) checkedNode.getObject();
                cn.cpoet.ideas.iu.actions.patch.model.GenPatchItem patchItem = new cn.cpoet.ideas.iu.actions.patch.model.GenPatchItem();
                patchItem.setModule(module);
                patchItem.setSourceFile(sourceFile);
                patchItem.setOutputFile(outputFile);
                patch.getDesc()
                        .append("\n").append(outputFile.getName())
                        .append("\t\t").append(module.getName())
                        .append("\t\t").append(sourceFile.getPath());
                patch.getItems().add(patchItem);
            }
        }
        doGenerate(setting, patch);
    }

    protected void doGenerate(GenPatchSetting setting, GenPatch patch) {
        String filePath = patch.getOutputPath().getPath() + "/aaa.zip";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            List<cn.cpoet.ideas.iu.actions.patch.model.GenPatchItem> items = patch.getItems();
            for (GenPatchItem item : items) {
                VirtualFile outputFile = item.getOutputFile();
                InputStream inputStream = outputFile.getInputStream();
                ZipEntry zipEntry = new ZipEntry(outputFile.getName());
                byte[] bytes = FileUtil.loadBytes(inputStream);
                zipEntry.setSize(bytes.length);
                zipEntry.setComment(outputFile.getPath());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(bytes);
                inputStream.close();
            }
            byte[] bytes = patch.getDesc().toString().getBytes();
            ZipEntry zipEntry = new ZipEntry("README.txt");
            zipEntry.setSize(bytes.length);
            zipEntry.setComment("README FILE");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);
        } catch (IOException e) {
        }
        dialogWrapper.close(DialogWrapper.OK_EXIT_CODE);
    }

    protected TreeNodeInfo[] getTreeCheckedNodes() {
        GenPatchTree tree = treePanel.getTree();
        return tree.getCheckedNodes(TreeNodeInfo.class, (nodeInfo) -> true);
    }

    public Action buildPreviewAction() {
        previewAction = new TextAction(I18n.t("actions.patch.GenPatchPackageAction.preview")) {

            private static final long serialVersionUID = 1542378595944056560L;

            @Override
            public void actionPerformed(ActionEvent e) {
                preview();
            }
        };
        previewAction.setEnabled(false);
        return previewAction;
    }


    public void preview() {

    }
}
