package cn.cpoet.ideas.actions.patch.component;

import cn.cpoet.ideas.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.actions.patch.constant.GenPatchConst;
import cn.cpoet.ideas.actions.patch.constant.GenPatchProjectTypeEnum;
import cn.cpoet.ideas.actions.patch.model.GenPatch;
import cn.cpoet.ideas.actions.patch.model.GenPatchItem;
import cn.cpoet.ideas.actions.patch.model.GenPatchModule;
import cn.cpoet.ideas.actions.patch.setting.GenPatchSetting;
import cn.cpoet.ideas.exception.IdeasException;
import cn.cpoet.ideas.model.FileInfo;
import cn.cpoet.ideas.model.TreeNodeInfo;
import cn.cpoet.ideas.util.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.spring.SpringLibraryUtil;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.JBSplitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 生成补丁抽象类
 *
 * @author CPoet
 */
public abstract class AbstractGenPatchPanel extends JBSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenPatchPanel.class);

    protected final Project project;
    protected final GenPatchSetting setting;

    public AbstractGenPatchPanel(Project project) {
        this.project = project;
        this.setting = GenPatchSetting.getInstance(project);
    }

    protected String doGenerate(GenPatch patch) {
        GenPatchSetting.State state = setting.getState();
        if (state.compress) {
            return doGenerateCompress(patch);
        }
        String path = getWriteFilePath(patch);
        List<GenPatchItem> items = patch.getItems();
        for (GenPatchItem item : items) {
            String filePath = path;
            if (state.includePath) {
                filePath = FilenameUtils.concat(filePath, item.getPatchModule().getModule().getName());
                filePath = FilenameUtils.concat(filePath, item.getFullPath());
            }
            FileUtil.writeToFile(item.getOutputFile(), FilenameUtils.concat(filePath, item.getOutputFile().getName()));
            doWriteAttachOutputFilesToFile(item, filePath);
        }
        doWriteReadmeFileToFile(patch, path);
        return path;
    }

    protected String getWriteFilePath(GenPatch patch) {
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

    protected void doWriteAttachOutputFilesToFile(GenPatchItem patchItem, String filePath) {
        if (CollectionUtils.isNotEmpty(patchItem.getAttachOutputFiles())) {
            for (VirtualFile attach : patchItem.getAttachOutputFiles()) {
                FileUtil.writeToFile(attach, FilenameUtils.concat(filePath, attach.getName()));
            }
        }
    }

    protected void doWriteReadmeFileToFile(GenPatch patch, String path) {
        String filePath = FilenameUtils.concat(path, "README.txt");
        FileUtil.writeToFile(patch.getDesc().toString().getBytes(), filePath);
    }

    protected String doGenerateCompress(GenPatch patch) {
        String filePath = getWriteFileName(patch);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            List<GenPatchItem> items = patch.getItems();
            for (GenPatchItem item : items) {
                doWritePatchItemToZip(zipOutputStream, item);
                doWriteAttachOutputFilesToZip(zipOutputStream, item);
            }
            doWriteReadmeFileToZip(zipOutputStream, patch);
        } catch (IOException e) {
            throw new IdeasException("Patch generate fail", e);
        }
        return filePath;
    }

    protected String getWriteFileName(GenPatch patch) {
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

    protected void doWriteAttachOutputFilesToZip(ZipOutputStream zipOutputStream, GenPatchItem item) {
        if (CollectionUtils.isNotEmpty(item.getAttachOutputFiles())) {
            for (VirtualFile attach : item.getAttachOutputFiles()) {
                doWritePatchItemToZip(zipOutputStream, item, attach);
            }
        }
    }

    protected void doWriteReadmeFileToZip(ZipOutputStream zipOutputStream, GenPatch patch) {
        ZipEntry zipEntry = new ZipEntry(GenPatchConst.PATCH_DESC_FILE_NAME);
        zipEntry.setComment(GenPatchConst.PATCH_DESC_FILE_COMMENT);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, patch.getDesc().toString().getBytes());
    }

    protected void doWritePatchItemToZip(ZipOutputStream zipOutputStream, GenPatchItem patchItem) {
        VirtualFile outputFile = patchItem.getOutputFile();
        doWritePatchItemToZip(zipOutputStream, patchItem, outputFile);
    }

    protected void doWritePatchItemToZip(ZipOutputStream zipOutputStream, GenPatchItem patchItem, VirtualFile file) {
        ZipEntry zipEntry = createZipEntry(patchItem, file);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, file);
    }

    protected ZipEntry createZipEntry(GenPatchItem patchItem, VirtualFile file) {
        GenPatchSetting.State state = setting.getState();
        GenPatchModule patchModule = patchItem.getPatchModule();
        ZipEntry zipEntry;
        if (state.includePath) {
            String filePath = String.join(FileUtil.UNIX_SEPARATOR, patchModule.getModule().getName(), patchItem.getFullPath(), file.getName());
            zipEntry = new ZipEntry(filePath);
        } else {
            zipEntry = new ZipEntry(file.getName());
        }
        zipEntry.setComment(file.getPath());
        return zipEntry;
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
        String patchDesc = getPatchDesc();
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
                    String relativePath = FileUtil.removeStartSeparator(fileInfo.getOutputRelativePath());
                    patchItem.setFullPath(FilenameUtils.getFullPathNoEndSeparator(relativePath));
                    patch.getDesc().append("\n");
                    if (state.includePath) {
                        patch.getDesc().append(module.getName())
                                .append(FileUtil.UNIX_SEPARATOR).append(patchItem.getFullPath())
                                .append(FileUtil.UNIX_SEPARATOR).append(outputFile.getName());
                        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                            if (patchModule.isApp()) {
                                patch.getDesc().append("\t\t").append(SpringUtil.SB_CLASSES_PATH);
                            } else {
                                patch.getDesc().append("\t\t").append(SpringUtil.SB_LIB_PATH);
                            }
                        }
                    } else {
                        patch.getDesc().append(outputFile.getName());
                        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                            if (patchModule.isApp()) {
                                patch.getDesc().append("\t\t").append(SpringUtil.SB_CLASSES_PATH);
                            } else {
                                patch.getDesc().append("\t\t").append(SpringUtil.SB_LIB_PATH)
                                        .append(FileUtil.UNIX_SEPARATOR).append(module.getName());
                            }
                        } else {
                            patch.getDesc().append("\t\t").append(module.getName());
                        }
                        patch.getDesc().append("\t\t").append(patchItem.getFullPath());
                    }
                    handleGenPatchItemAttachOutputFiles(patchItem);
                    patch.getItems().add(patchItem);
                }
            }
        }
        return patch;
    }

    protected void handleGenPatchItemAttachOutputFiles(GenPatchItem patchItem) {
        VirtualFile[] innerOutputFiles = ClassUtil.getInnerOutputFiles(patchItem.getOutputFile());
        for (VirtualFile innerOutputFile : innerOutputFiles) {
            if (patchItem.getAttachOutputFiles() == null) {
                patchItem.setAttachOutputFiles(new LinkedList<>());
            }
            patchItem.getAttachOutputFiles().add(innerOutputFile);
        }
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
        patch.setFileName(getFileName());
        if (SpringLibraryUtil.hasSpringLibrary(project)) {
            patch.setProjectType(GenPatchProjectTypeEnum.SPRING);
        } else {
            patch.setProjectType(GenPatchProjectTypeEnum.NONE);
        }
        return patch;
    }

    protected abstract String getPatchDesc();

    protected abstract String getFileName();

    protected abstract TreeNodeInfo[] getTreeCheckedNodes();
}
