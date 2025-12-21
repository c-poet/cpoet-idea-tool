package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.constant.FileBuildTypeExtEnum;
import cn.cpoet.tool.exception.ToolException;
import cn.cpoet.tool.model.FileInfo;
import cn.cpoet.tool.model.TreeNodeInfo;
import cn.cpoet.tool.setting.Setting;
import cn.cpoet.tool.util.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.PotemkinProgress;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.spring.SpringLibraryUtil;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBDimension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(GenPatchPanel.class);

    private Action previewAction;
    private final Project project;
    private final GenPatchSetting setting;
    private final DialogWrapper dialogWrapper;
    private final AtomicInteger checkedCount;
    private final GenPatchConfPanel confPanel;
    private final GenPatchTreePanel treePanel;

    public GenPatchPanel(Project project, Object[] selectedItems, DialogWrapper dialogWrapper) {
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
        treePanel = new GenPatchTreePanel(project, selectedItems);
        setFirstComponent(treePanel);
        confPanel = new GenPatchConfPanel(project, this);
        setSecondComponent(confPanel);
        checkedCount = new AtomicInteger(getTreeCheckedNodes().length);
        addCheckboxTreeListener();
    }

    private void addCheckboxTreeListener() {
        treePanel.getTree().addCheckboxTreeListener(new CheckboxTreeListener() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                checkedCount.getAndAdd(node.isChecked() ? 1 : -1);
                updateBtnStatus();
            }
        });
        updateBtnStatus();
    }

    protected void buildPreviewAction() {
        previewAction = new TextAction(I18nUtil.t("actions.patch.GenPatchPackageAction.preview")) {
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
        ProgressIndicator indicator = startGenerateIndicator();
        GenPatchSetting.State state = setting.getState();
        indicator.setFraction(0.1);
        indicator.setText("Generate Patch info");
        getGenPatch()
                .then(patch -> {
                    indicator.setText("Generate patch");
                    indicator.setFraction(0.5);
                    return doGenerate(patch);
                })
                .onSuccess((path) -> {
                    indicator.setText("Generate after");
                    indicator.setFraction(0.8);
                    if (state.openOutputFolder) {
                        String patchPath = FilenameUtils.separatorsToSystem(path);
                        if (state.compress) {
                            FileUtil.selectFile(patchPath);
                        } else {
                            FileUtil.openFolder(patchPath);
                        }
                    }
                    state.lastFileNamePrefix = confPanel.getFileNamePrefix();
                    state.lastFileName = confPanel.getFileName();
                    indicator.setFraction(0.98);
                })
                .onError(e -> {
                    LOGGER.error("生成补丁失败: {}", e.getMessage(), e);
                    NotificationUtil.initBalloonError(e.getMessage()).notify(project);
                })
                .onProcessed(path -> {
                    stopGenerateIndicator(indicator);
                    if (StringUtils.isNotEmpty(path)) {
                        doOpenReplacePatch(path);
                    }
                });
    }

    protected void doOpenReplacePatch(String path) {
        if (!setting.getState().openReplacePatch) {
            return;
        }
        String patchAssistant2JPath = Setting.getInstance().getState().patchAssistant2JPath;
        if (StringUtils.isBlank(patchAssistant2JPath)) {
            NotificationUtil.initBalloonError("The PatchAssistant2J path is not configured").notify(project);
            return;
        }
        try {
            Runtime.getRuntime().exec(new String[]{patchAssistant2JPath, "--patch=" + path});
        } catch (Exception e) {
            NotificationUtil.initBalloonError("Failed to launch PatchAssistant2J, please check if the path configuration is correct").notify(project);
        }
    }

    protected ProgressIndicator startGenerateIndicator() {
        PotemkinProgress progress = new PotemkinProgress("Generating", project, dialogWrapper.getContentPanel(), null);
        dialogWrapper.getWindow().setEnabled(false);
        progress.start();
        return progress;
    }

    protected void stopGenerateIndicator(ProgressIndicator progress) {
        progress.stop();
        dialogWrapper.getWindow().setEnabled(true);
    }

    public void preview() {
    }

    protected void updateBtnStatus() {
        GenPatchSetting.State state = setting.getState();
        if (checkedCount.get() > 0
                && StringUtils.isNotBlank(state.outputFolder)
                && StringUtils.isNotBlank(confPanel.getFileName())) {
            dialogWrapper.setOKActionEnabled(true);
        } else {
            dialogWrapper.setOKActionEnabled(false);
        }
    }


    protected String doGenerate(GenPatchBean patch) {
        GenPatchSetting.State state = setting.getState();
        if (state.compress) {
            return doGenerateCompress(patch);
        }
        String path = getWriteFilePath(patch);
        List<GenPatchItemBean> items = patch.getItems();
        for (GenPatchItemBean item : items) {
            String filePath = path;
            if (state.includePath) {
                if (!item.getPatchModule().isApp()) {
                    filePath = FilenameUtils.concat(filePath, item.getPatchModule().getModule().getName());
                }
                filePath = FilenameUtils.concat(filePath, item.getFullPath());
            }
            FileUtil.writeToFile(item.getOutputFile(), FilenameUtils.concat(filePath, item.getOutputFile().getName()));
            doWriteAttachOutputFilesToFile(item, filePath);
        }
        doWriteReadmeFileToFile(patch, path);
        return path;
    }

    protected String getWriteFilePath(GenPatchBean patch) {
        String path = FilenameUtils.concat(patch.getOutputFolder(), patch.getFileName());
        File file = new File(path);
        if (!file.exists()) {
            return path;
        }
        if (setting.getState().cover) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (Exception e) {
                LOGGER.error("File deletion failed: {}", file.getPath(), e);
            }
            return path;
        }
        int i = 0;
        do {
            file = new File(path + "(" + i++ + ")");
        } while (file.exists());
        return file.getPath();
    }

    protected void doWriteAttachOutputFilesToFile(GenPatchItemBean patchItem, String filePath) {
        if (CollectionUtils.isNotEmpty(patchItem.getAttachOutputFiles())) {
            for (VirtualFile attach : patchItem.getAttachOutputFiles()) {
                FileUtil.writeToFile(attach, FilenameUtils.concat(filePath, attach.getName()));
            }
        }
    }

    protected void doWriteReadmeFileToFile(GenPatchBean patch, String path) {
        String filePath = FilenameUtils.concat(path, "README.txt");
        FileUtil.writeToFile(patch.getDesc().toString().getBytes(), filePath);
    }

    protected String doGenerateCompress(GenPatchBean patch) {
        String filePath = getWriteFileName(patch);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            List<GenPatchItemBean> items = patch.getItems();
            for (GenPatchItemBean item : items) {
                doWritePatchItemToZip(zipOutputStream, item);
                doWriteAttachOutputFilesToZip(zipOutputStream, item);
            }
            doWriteReadmeFileToZip(zipOutputStream, patch);
        } catch (IOException e) {
            throw new ToolException("Patch generate fail", e);
        }
        return filePath;
    }

    protected String getWriteFileName(GenPatchBean patch) {
        String filePath = FilenameUtils.concat(patch.getOutputFolder(), patch.getFileName());
        File file = new File(filePath + GenPatchConst.PATCH_FULL_FILE_EXT);
        if (!file.exists()) {
            return file.getPath();
        }
        if (setting.getState().cover) {
            if (!file.delete()) {
                LOGGER.warn("File deletion failed:{}", file.getPath());
            }
            return file.getPath();
        }
        int i = 0;
        do {
            file = new File(filePath + "(" + i++ + ")" + GenPatchConst.PATCH_FULL_FILE_EXT);
        } while (file.exists());
        return file.getPath();
    }

    protected void doWriteAttachOutputFilesToZip(ZipOutputStream zipOutputStream, GenPatchItemBean item) {
        if (CollectionUtils.isNotEmpty(item.getAttachOutputFiles())) {
            for (VirtualFile attach : item.getAttachOutputFiles()) {
                doWritePatchItemToZip(zipOutputStream, item, attach);
            }
        }
    }

    protected void doWriteReadmeFileToZip(ZipOutputStream zipOutputStream, GenPatchBean patch) {
        ZipEntry zipEntry = new ZipEntry(GenPatchConst.PATCH_DESC_FILE_NAME);
        zipEntry.setComment(GenPatchConst.PATCH_DESC_FILE_COMMENT);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, patch.getDesc().toString().getBytes());
    }

    protected void doWritePatchItemToZip(ZipOutputStream zipOutputStream, GenPatchItemBean patchItem) {
        VirtualFile outputFile = patchItem.getOutputFile();
        doWritePatchItemToZip(zipOutputStream, patchItem, outputFile);
    }

    protected void doWritePatchItemToZip(ZipOutputStream zipOutputStream, GenPatchItemBean patchItem, VirtualFile file) {
        ZipEntry zipEntry = createZipEntry(patchItem, file);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, file);
    }

    protected ZipEntry createZipEntry(GenPatchItemBean patchItem, VirtualFile file) {
        GenPatchSetting.State state = setting.getState();
        GenPatchModuleBean patchModule = patchItem.getPatchModule();
        ZipEntry zipEntry;
        if (state.includePath) {
            String filePath;
            if (patchModule.isApp()) {
                filePath = String.join(FileUtil.UNIX_SEPARATOR, patchItem.getFullPath(), file.getName());
            } else {
                filePath = String.join(FileUtil.UNIX_SEPARATOR, patchModule.getModule().getName(), patchItem.getFullPath(), file.getName());
            }
            zipEntry = new ZipEntry(filePath);
        } else {
            zipEntry = new ZipEntry(file.getName());
        }
        zipEntry.setComment(file.getPath());
        return zipEntry;
    }


    protected Promise<GenPatchBean> getGenPatch() {
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

    protected Promise<GenPatchBean> getGenPatchProject() {
        return ProjectTaskManager.getInstance(project)
                .rebuildAllModules()
                .then(result -> doGetGenPatch(getTreeCheckedNodes()));
    }

    protected Promise<GenPatchBean> getGenPatchModule() {
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

    protected Promise<GenPatchBean> getGenPatchFile() {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        VirtualFile[] files = Arrays.stream(checkedNodes)
                .map(nodeInfo -> (VirtualFile) nodeInfo.getObject())
                .toArray(VirtualFile[]::new);
        return ProjectTaskManager.getInstance(project)
                .compile(files)
                .then(result -> doGetGenPatch(checkedNodes));
    }

    protected GenPatchBean doGetGenPatch(TreeNodeInfo[] treeNodeInfos) {
        GenPatchBean patch = createGenPatch();
        patch.getDesc().append("File Name: ").append(patch.getFileName());
        String patchDesc = getPatchDesc();
        if (StringUtils.isNotBlank(patchDesc)) {
            patch.getDesc().append('\n').append("Patch Desc:\n").append(patchDesc);
        }
        patch.getDesc().append("\n\n").append("File Paths:");
        Map<GenPatchModuleBean, List<TreeNodeInfo>> moduleFilesMapping = getModuleFilesMapping(patch, treeNodeInfos);
        for (Map.Entry<GenPatchModuleBean, List<TreeNodeInfo>> entry : moduleFilesMapping.entrySet()) {
            for (TreeNodeInfo nodeInfo : entry.getValue()) {
                addPatchItem(patch, entry.getKey(), (VirtualFile) nodeInfo.getObject());
            }
        }
        return patch;
    }

    private void addPatchItem(GenPatchBean patch, GenPatchModuleBean patchModule, VirtualFile file) {
        FileInfo fileInfo = FileUtil.getFileInfo(patchModule.getModule(), file);
        // 非编译文件可使用源文件
        // 编译文件从输出文件读取
        if (fileInfo.getOutputFile() != null) {
            VirtualFile sourceFile = fileInfo.getSourceFile();
            VirtualFile outputFile = fileInfo.getOutputFile();
            GenPatchItemBean patchItem = new GenPatchItemBean();
            patchItem.setPatchModule(patchModule);
            patchItem.setSourceFile(sourceFile);
            patchItem.setOutputFile(outputFile);
            String relativePath = FileUtil.removeStartSeparator(fileInfo.getOutputRelativePath());
            patchItem.setFullPath(FilenameUtils.getFullPathNoEndSeparator(relativePath));
            addPatchReplacePathInfo(patch, patchItem);
            addInner2AttachOutFiles(patchItem);
            patch.getItems().add(patchItem);
            addMapStructMapperImpl(patch, patchItem);
        }
    }

    private void addPatchReplacePathInfo(GenPatchBean patch, GenPatchItemBean patchItem) {
        patch.getDesc().append("\n");
        GenPatchModuleBean patchModule = patchItem.getPatchModule();
        if (setting.getState().includePath) {
            if (!patchItem.getPatchModule().isApp()) {
                patch.getDesc().append(patchModule.getModule().getName()).append(FileUtil.UNIX_SEPARATOR);
            }
            patch.getDesc().append(patchItem.getFullPath())
                    .append(FileUtil.UNIX_SEPARATOR).append(patchItem.getOutputFile().getName());
            if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                if (patchModule.isApp()) {
                    patch.getDesc().append("\t\t").append(SpringUtil.SB_CLASSES_PATH);
                } else {
                    patch.getDesc().append("\t\t").append(SpringUtil.SB_LIB_PATH);
                }
            }
        } else {
            patch.getDesc().append(patchItem.getOutputFile().getName());
            if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                if (patchModule.isApp()) {
                    patch.getDesc().append("\t\t").append(SpringUtil.SB_CLASSES_PATH);
                } else {
                    patch.getDesc().append("\t\t").append(SpringUtil.SB_LIB_PATH)
                            .append(FileUtil.UNIX_SEPARATOR).append(patchModule.getModule().getName());
                }
            } else {
                patch.getDesc().append("\t\t").append(patchModule.getModule().getName());
            }
            patch.getDesc().append("\t\t").append(patchItem.getFullPath());
        }
    }

    private void addInner2AttachOutFiles(GenPatchItemBean patchItem) {
        VirtualFile[] innerOutputFiles = ClassUtil.getInnerOutputFiles(patchItem.getOutputFile());
        for (VirtualFile innerOutputFile : innerOutputFiles) {
            patchItem.getAndInitAttachOutputFiles().add(innerOutputFile);
        }
    }

    private void addMapStructMapperImpl(GenPatchBean patch, GenPatchItemBean patchItem) {
        VirtualFile sourceFile = patchItem.getSourceFile();
        FileBuildTypeExtEnum fileExt = MapStructUtil.getSupportBuildTypeExt(sourceFile);
        if (fileExt == null) {
            return;
        }
        PsiJavaFile psiFile = Objects.requireNonNull((PsiJavaFile) PsiManager.getInstance(project).findFile(sourceFile));
        PsiClass[] classes = psiFile.getClasses();
        for (PsiClass psiClass : classes) {
            String mapperImplName = MapStructUtil.getMapperImplName(psiClass);
            if (StringUtils.isBlank(mapperImplName)) {
                continue;
            }
            String filePath = ClassUtil.convertNameToPath(mapperImplName) + FilenameUtils.EXTENSION_SEPARATOR + fileExt.getSourceExt();
            VirtualFile mapperImplFile = FileUtil.getSourceFile(patchItem.getPatchModule().getModule(), filePath);
            // Idea未开启Annotation或者未引入MapStruct Processor坐标的情况下，不自动生成源码
            if (mapperImplFile == null) {
                continue;
            }
            addPatchItem(patch, patchItem.getPatchModule(), mapperImplFile);
        }
    }

    protected Map<GenPatchModuleBean, List<TreeNodeInfo>> getModuleFilesMapping(GenPatchBean patch, TreeNodeInfo[] treeNodeInfos) {
        Map<Module, GenPatchModuleBean> patchModuleCache = new HashMap<>();
        Map<GenPatchModuleBean, List<TreeNodeInfo>> moduleFilesMapping = new HashMap<>();
        for (TreeNodeInfo checkedNode : treeNodeInfos) {
            Module module = TreeUtil.findModule(checkedNode);
            GenPatchModuleBean patchModule = patchModuleCache.computeIfAbsent(module, k -> createGenPatchModule(k, patch));
            moduleFilesMapping.computeIfAbsent(patchModule, k -> new LinkedList<>()).add(checkedNode);
        }
        return moduleFilesMapping;
    }

    protected GenPatchModuleBean createGenPatchModule(Module module, GenPatchBean patch) {
        GenPatchModuleBean patchModule = new GenPatchModuleBean();
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

    protected GenPatchBean createGenPatch() {
        GenPatchSetting.State state = setting.getState();
        GenPatchBean patch = new GenPatchBean();
        patch.setOutputFolder(state.outputFolder);
        patch.setFileName(getFileName());
        if (SpringLibraryUtil.hasSpringLibrary(project)) {
            patch.setProjectType(GenPatchProjectTypeEnum.SPRING);
        } else {
            patch.setProjectType(GenPatchProjectTypeEnum.NONE);
        }
        return patch;
    }

    protected String getPatchDesc() {
        return treePanel.getPatchDesc();
    }

    protected String getFileName() {
        return confPanel.getFileName();
    }

    protected TreeNodeInfo[] getTreeCheckedNodes() {
        GenPatchTree tree = treePanel.getTree();
        return tree.getCheckedNodes(TreeNodeInfo.class, (nodeInfo) -> true);
    }
}
