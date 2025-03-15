package cn.cpoet.tool.actions.patch.model;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * @author CPoet
 */
public class GenPatchItem {

    /** 文件相对路径（不含文件名） */
    private String fullPath;

    /** 文件所在的模块 */
    private GenPatchModule patchModule;

    /** 源文件 */
    private VirtualFile sourceFile;

    /** 输出文件 */
    private VirtualFile outputFile;

    /** 附加输出文件：例如JAVA内部类 */
    private List<VirtualFile> attachOutputFiles;

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public GenPatchModule getPatchModule() {
        return patchModule;
    }

    public void setPatchModule(GenPatchModule patchModule) {
        this.patchModule = patchModule;
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

    public List<VirtualFile> getAttachOutputFiles() {
        return attachOutputFiles;
    }

    public void setAttachOutputFiles(List<VirtualFile> attachOutputFiles) {
        this.attachOutputFiles = attachOutputFiles;
    }
}
