package cn.cpoet.ideas.actions.patch.component;

import cn.cpoet.ideas.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.actions.patch.constant.GenPatchConst;
import cn.cpoet.ideas.actions.patch.constant.GenPatchProjectTypeEnum;
import cn.cpoet.ideas.actions.patch.model.GenPatch;
import cn.cpoet.ideas.actions.patch.model.GenPatchItem;
import cn.cpoet.ideas.actions.patch.model.GenPatchModule;
import cn.cpoet.ideas.actions.patch.setting.GenPatchSetting;
import cn.cpoet.ideas.exception.IdeasException;
import cn.cpoet.ideas.i18n.I18n;
import cn.cpoet.ideas.model.FileInfo;
import cn.cpoet.ideas.model.TreeNodeInfo;
import cn.cpoet.ideas.util.ModuleUtil;
import cn.cpoet.ideas.util.NotificationUtil;
import cn.cpoet.ideas.util.TreeUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spring.SpringLibraryUtil;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBDimension;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.*;
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
    /** 配置信息 */
    private final GenPatchSetting setting;
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
        this.setting = GenPatchSetting.getInstance(project);
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
        GenPatchBuildTypeEnum buildTypeEnum = GenPatchBuildTypeEnum.ofCode(setting.getState().buildType);
        switch (buildTypeEnum) {
            case PROJECT:
                return getGenPatchProject();
            case MODULE:
                return getGenPatchModule();
            case FILE:
                return getGenPatchFile();
            default:
                TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
                return Promises.runAsync(() -> doGetGenPatch(checkedNodes));
        }
    }

    protected Promise<GenPatch> getGenPatchProject() {
        return ProjectTaskManager.getInstance(project)
                .rebuildAllModules()
                .then(result -> doGetGenPatch(getTreeCheckedNodes()));
    }

    protected Promise<GenPatch> getGenPatchModule() {
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
                .then(ret -> doGetGenPatch(checkedNodes));
    }

    protected Promise<GenPatch> getGenPatchFile() {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        VirtualFile[] files = Arrays.stream(checkedNodes)
                .map(nodeInfo -> (VirtualFile) nodeInfo.getObject())
                .toArray(VirtualFile[]::new);
        return ProjectTaskManager.getInstance(project)
                .compile(files)
                .then(result -> doGetGenPatch(checkedNodes));
    }

    protected GenPatch doGetGenPatch(TreeNodeInfo[] treeNodeInfos) {
        GenPatchSetting.State state = setting.getState();
        GenPatch patch = createGenPatch();
        String patchDesc = treePanel.getPatchDesc();
        if (StringUtils.isNotBlank(patchDesc)) {
            patch.getDesc().append(patchDesc).append("\n\n");
        }
        patch.getDesc().append("Files path:");
        Map<GenPatchModule, List<TreeNodeInfo>> moduleFilesMapping = getModuleFilesMapping(patch, treeNodeInfos);
        for (Map.Entry<GenPatchModule, List<TreeNodeInfo>> entry : moduleFilesMapping.entrySet()) {
            GenPatchModule patchModule = entry.getKey();
            Module module = patchModule.getModule();
            for (TreeNodeInfo nodeInfo : entry.getValue()) {
                FileInfo fileInfo = ModuleUtil.getFileInfo(module, (VirtualFile) nodeInfo.getObject());
                if (fileInfo.getOutputFile() != null) {
                    VirtualFile sourceFile = fileInfo.getSourceFile();
                    VirtualFile outputFile = fileInfo.getOutputFile();
                    GenPatchItem patchItem = new GenPatchItem();
                    patchItem.setPatchModule(patchModule);
                    patchItem.setSourceFile(sourceFile);
                    patchItem.setOutputFile(outputFile);
                    String relativePath = cn.cpoet.ideas.util.FileUtil.removeStartSeparator(fileInfo.getOutputRelativePath());
                    patchItem.setFullPath(FilenameUtils.getFullPathNoEndSeparator(relativePath));
                    patch.getDesc().append("\n");
                    if (state.includePath) {
                        patch.getDesc().append(module.getName())
                                .append('/').append(patchItem.getFullPath())
                                .append('/').append(outputFile.getName());
                        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                            if (patchModule.isApp()) {
                                patch.getDesc().append("\t\t").append("BOOT-INF/classes");
                            } else {
                                patch.getDesc().append("\t\t").append("BOOT-INF/lib");
                            }
                        }
                    } else {
                        patch.getDesc().append(outputFile.getName());
                        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                            if (patchModule.isApp()) {
                                patch.getDesc().append("\t\t").append("BOOT-INF/classes");
                            } else {
                                patch.getDesc().append("\t\t").append("BOOT-INF/lib/").append(module.getName());
                            }
                        } else {
                            patch.getDesc().append("\t\t").append(module.getName());
                        }
                        patch.getDesc().append("\t\t").append(patchItem.getFullPath());
                    }
                    patch.getItems().add(patchItem);
                }
            }
        }
        return patch;
    }

    protected Map<GenPatchModule, List<TreeNodeInfo>> getModuleFilesMapping(GenPatch patch, TreeNodeInfo[] treeNodeInfos) {
        Map<Module, GenPatchModule> patchModuleCache = new HashMap<>();
        Map<GenPatchModule, List<TreeNodeInfo>> moduleFilesMapping = new HashMap<>();
        for (TreeNodeInfo checkedNode : treeNodeInfos) {
            Module module = TreeUtil.findModule(checkedNode);
            GenPatchModule patchModule = patchModuleCache.computeIfAbsent(module, k -> createGenPatchModule(k, patch));
            moduleFilesMapping.computeIfAbsent(patchModule, k -> new LinkedList<>()).add(checkedNode);
        }
        return moduleFilesMapping;
    }

    protected GenPatchModule createGenPatchModule(Module module, GenPatch patch) {
        GenPatchModule patchModule = new GenPatchModule();
        patchModule.setModule(module);
        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
            SpringManager springManager = SpringManager.getInstance(project);
            Set<SpringModel> springModels = springManager.getAllModelsWithoutDependencies(module);
            patchModule.setApp(CollectionUtils.isNotEmpty(springModels));
        } else {
            patchModule.setApp(false);
        }
        return patchModule;
    }

    protected GenPatch createGenPatch() {
        GenPatchSetting.State state = setting.getState();
        GenPatch patch = new GenPatch();
        patch.setOutputFolder(state.outputFolder);
        patch.setFileName(confPanel.getFileName());
        if (SpringLibraryUtil.hasSpringLibrary(project)) {
            patch.setProjectType(GenPatchProjectTypeEnum.SPRING);
        } else {
            patch.setProjectType(GenPatchProjectTypeEnum.NONE);
        }
        return patch;
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
    }

    public Action getPreviewAction() {
        return previewAction;
    }

    public void generate() {
        dialogWrapper.setOKActionEnabled(false);
        GenPatchSetting.State state = setting.getState();
        BackgroundableProcessIndicator processIndicator = new BackgroundableProcessIndicator(project, "Generate patch",
                null, null, false);
        ProgressManager progressManager = ProgressManager.getInstance();
        progressManager.runProcess(() -> getGenPatch()
                .then(this::doGenerate)
                .onSuccess((path) -> {
                    if (state.openOutputFolder) {
                        String patchPath = FilenameUtils.separatorsToSystem(path);
                        cn.cpoet.ideas.util.FileUtil.selectFile(patchPath);
                    }
                    state.lastFileNamePrefix = confPanel.getFileNamePrefix();
                    state.lastFileName = confPanel.getFileName();
                    processIndicator.setText("Generate success");
                })
                .onError(e -> {
                    NotificationUtil.getBalloonGroup()
                            .createNotification(e.getMessage(), NotificationType.ERROR)
                            .notify(project);
                })
                .onProcessed(patch -> {
                    processIndicator.stop();
                    dialogWrapper.setOKActionEnabled(true);
                }), processIndicator);

    }

    public void preview() {
    }

    protected String doGenerate(GenPatch patch) {
        String filePath = FilenameUtils.concat(patch.getOutputFolder(), patch.getFileName() + GenPatchConst.PATCH_FULL_FILE_EXT);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            List<GenPatchItem> items = patch.getItems();
            for (GenPatchItem item : items) {
                writePatchItem(zipOutputStream, item);
            }
            writeReadmeFile(zipOutputStream, patch);
        } catch (IOException e) {
            throw new IdeasException("Patch generate fail", e);
        }
        return filePath;
    }

    protected void writeReadmeFile(ZipOutputStream zipOutputStream, GenPatch patch) {
        try {
            byte[] bytes = patch.getDesc().toString().getBytes();
            ZipEntry zipEntry = new ZipEntry("README.txt");
            zipEntry.setSize(bytes.length);
            zipEntry.setComment("README FILE");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);
        } catch (Exception e) {
            throw new IdeasException("Write patch readme file fail", e);
        }
    }

    protected void writePatchItem(ZipOutputStream zipOutputStream, GenPatchItem patchItem) {
        VirtualFile outputFile = patchItem.getOutputFile();
        try (InputStream inputStream = outputFile.getInputStream()) {
            ZipEntry zipEntry = createZipEntry(patchItem);
            byte[] bytes = FileUtil.loadBytes(inputStream);
            zipEntry.setSize(bytes.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);
        } catch (Exception e) {
            throw new IdeasException("Write patch item fail", e);
        }
    }

    protected ZipEntry createZipEntry(GenPatchItem patchItem) {
        GenPatchSetting.State state = setting.getState();
        VirtualFile outputFile = patchItem.getOutputFile();
        GenPatchModule patchModule = patchItem.getPatchModule();
        ZipEntry zipEntry;
        if (state.includePath) {
            String filePath = patchModule.getModule().getName() + "/" + patchItem.getFullPath() + "/" + outputFile.getName();
            zipEntry = new ZipEntry(filePath);
        } else {
            zipEntry = new ZipEntry(outputFile.getName());
        }
        zipEntry.setComment(outputFile.getPath());
        return zipEntry;
    }


    protected void updateBtnStatus() {
        GenPatchSetting.State state = setting.getState();
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
