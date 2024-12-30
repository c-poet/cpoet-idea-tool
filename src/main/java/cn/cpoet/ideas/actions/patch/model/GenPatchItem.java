package cn.cpoet.ideas.actions.patch.model;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author CPoet
 */
public class GenPatchItem {

    /** 文件所在的模块 */
    private Module module;

    /** 源文件 */
    private VirtualFile sourceFile;

    /** 输出文件 */
    private VirtualFile outputFile;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public VirtualFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(VirtualFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public VirtualFile getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(VirtualFile outputFile) {
        this.outputFile = outputFile;
    }
}
