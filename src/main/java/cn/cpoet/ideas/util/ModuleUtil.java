package cn.cpoet.ideas.util;

import cn.cpoet.ideas.model.FileInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * 模块工具
 *
 * @author CPoet
 */
public abstract class ModuleUtil {
    private ModuleUtil() {
    }

    public static VirtualFile getOutputFile(Module module, VirtualFile sourceFile) {
        return getFileInfo(module, sourceFile).getOutputFile();
    }

    public static FileInfo getFileInfo(Module module, VirtualFile sourceFile) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSourceFile(sourceFile);
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots();
        for (VirtualFile sourceRoot : sourceRoots) {
            if (FileUtil.isFileChild(sourceRoot, sourceFile)) {
                String outputFilePath = FileUtil.getOutputFilePath(sourceRoot, sourceFile);
                CompilerModuleExtension compilerModuleExtension = CompilerModuleExtension.getInstance(module);
                assert compilerModuleExtension != null;
                VirtualFile outputFile = FileUtil.getFileInRoot(compilerModuleExtension.getCompilerOutputPath(), outputFilePath);
                if (outputFile == null) {
                    outputFile = FileUtil.getFileInRoot(compilerModuleExtension.getCompilerOutputPathForTests(), outputFilePath);
                }
                fileInfo.setSourceRoot(sourceRoot);
                fileInfo.setOutputFile(outputFile);
                fileInfo.setOutputRelativePath(outputFilePath);
                break;
            }
        }
        return fileInfo;
    }
}
