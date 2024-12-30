package cn.cpoet.ideas.iu.actions.patch.component;

import cn.cpoet.ideas.ic.i18n.I18n;
import cn.cpoet.ideas.ic.model.FileInfo;
import cn.cpoet.ideas.ic.model.TreeNodeInfo;
import cn.cpoet.ideas.ic.util.ModuleUtil;
import cn.cpoet.ideas.ic.util.TreeUtil;
import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.iu.actions.patch.model.GenPatch;
import cn.cpoet.ideas.iu.actions.patch.model.GenPatchItem;
import cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBDimension;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;

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


    public GenPatchPanel(Project project, DialogWrapper dialogWrapper) {
        this.project = project;
        this.dialogWrapper = dialogWrapper;
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        setPreferredSize(new JBDimension(setting.getState().width, setting.getState().height));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setting.getState().width = getWidth();
                setting.getState().height = getHeight();
            }
        });
        buildPreviewAction();
        treePanel = new GenPatchTreePanel(project);
        setFirstComponent(treePanel);
        confPanel = new GenPatchConfPane(project, this);
        setSecondComponent(confPanel);
        checkedCount = new AtomicInteger(getTreeCheckedNodes().length);
        addCheckboxTreeListener();
    }

    private void addCheckboxTreeListener() {
        treePanel.getTree().addCheckboxTreeListener(new CheckboxTreeListener() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                if (node.isChecked()) {
                    checkedCount.incrementAndGet();
                } else {
                    checkedCount.decrementAndGet();
                }
                updateBtnStatus();
            }
        });
        updateBtnStatus();
    }

    protected Promise<GenPatch> getGenPatch() {
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        GenPatchBuildTypeEnum buildTypeEnum = GenPatchBuildTypeEnum.ofCode(setting.getState().buildType);
        switch (buildTypeEnum) {
            case PROJECT:
                return getGenPatchProject(setting);
            case MODULE:
                return getGenPatchModule(setting);
            case FILE:
                return getGenPatchFile(setting);
            default:
                TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
                return Promises.runAsync(() -> doGetGenPatch(setting, checkedNodes));
        }
    }

    protected Promise<GenPatch> getGenPatchProject(GenPatchSetting setting) {
        return ProjectTaskManager.getInstance(project)
                .rebuildAllModules()
                .then(result -> doGetGenPatch(setting, getTreeCheckedNodes()));
    }

    protected Promise<GenPatch> getGenPatchModule(GenPatchSetting setting) {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        Set<Module> modules = new HashSet<>();
        for (TreeNodeInfo checkedNode : checkedNodes) {
            Module module = TreeUtil.findModule(checkedNode);
            if (module != null) {
                modules.add(module);
            }
        }
        return ProjectTaskManager.getInstance(project)
                .rebuild(modules.toArray(Module[]::new))
                .then(ret -> doGetGenPatch(setting, checkedNodes));
    }

    protected Promise<GenPatch> getGenPatchFile(GenPatchSetting setting) {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        VirtualFile[] files = Arrays.stream(checkedNodes)
                .map(nodeInfo -> (VirtualFile) nodeInfo.getObject())
                .toArray(VirtualFile[]::new);
        return ProjectTaskManager.getInstance(project)
                .compile(files)
                .then(result -> doGetGenPatch(setting, checkedNodes));
    }

    protected GenPatch doGetGenPatch(GenPatchSetting setting, TreeNodeInfo[] treeNodeInfos) {
        GenPatchSetting.State state = setting.getState();
        GenPatch genPatch = new GenPatch();
        genPatch.setOutputFolder(state.outputFolder);
        genPatch.setFileName(confPanel.getFileName());
        genPatch.getDesc().append("路径:");
        for (TreeNodeInfo checkedNode : treeNodeInfos) {
            Module module = TreeUtil.findModule(checkedNode);
            FileInfo fileInfo = ModuleUtil.getFileInfo(module, (VirtualFile) checkedNode.getObject());
            if (fileInfo.getOutputFile() != null) {
                VirtualFile sourceFile = fileInfo.getSourceFile();
                VirtualFile outputFile = fileInfo.getOutputFile();
                GenPatchItem patchItem = new GenPatchItem();
                patchItem.setModule(module);
                patchItem.setSourceFile(sourceFile);
                patchItem.setOutputFile(outputFile);
                genPatch.getDesc()
                        .append("\n").append(outputFile.getName())
                        .append("\t").append(module.getName())
                        .append("\t").append(FilenameUtils.getFullPathNoEndSeparator(fileInfo.getOutputRelativePath()));
                genPatch.getItems().add(patchItem);
            }
        }
        return genPatch;
    }

    protected TreeNodeInfo[] getTreeCheckedNodes() {
        GenPatchTree tree = treePanel.getTree();
        return tree.getCheckedNodes(TreeNodeInfo.class, (nodeInfo) -> true);
    }

    protected void buildPreviewAction() {
        previewAction = new TextAction(I18n.t("actions.patch.GenPatchPackageAction.preview")) {
            private static final long serialVersionUID = 1542378595944056560L;

            @Override
            public void actionPerformed(ActionEvent e) {
                preview();
            }
        };
        previewAction.setEnabled(false);
    }

    public Action getPreviewAction() {
        return previewAction;
    }

    public void generate() {
        dialogWrapper.setOKActionEnabled(false);
        GenPatchSetting.State state = GenPatchSetting.getInstance(project).getState();
        getGenPatch()
                .then(this::doGenerate)
                .onSuccess((path) -> {
                    if (state.openOutputFolder) {
                        String patchPath = FilenameUtils.separatorsToSystem(path);
                        cn.cpoet.ideas.ic.util.FileUtil.selectFile(patchPath);
                    }
                })
                .onProcessed(patch -> dialogWrapper.setOKActionEnabled(true));
    }

    public void preview() {
        getGenPatch().onSuccess(patch -> {
            System.out.println(patch);
        });
    }

    protected String doGenerate(GenPatch patch) {
        String filePath = patch.getOutputFolder() + "/" + patch.getFileName() + ".zip";
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
            throw new RuntimeException("补丁包生成失败", e);
        }
        dialogWrapper.disposeIfNeeded();
        return filePath;
    }

    void updateBtnStatus() {
        GenPatchSetting.State state = GenPatchSetting.getInstance(project).getState();
        if (checkedCount.get() > 0
                && StringUtils.isNotBlank(state.outputFolder)
                && StringUtils.isNotBlank(confPanel.getFileName())) {
            dialogWrapper.setOKActionEnabled(true);
            previewAction.setEnabled(true);
        } else {
            dialogWrapper.setOKActionEnabled(false);
            previewAction.setEnabled(false);
        }
    }
}
