package cn.cpoet.ideas.ic.util;

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
                return outputFile;
            }
        }
        return null;
    }
}
