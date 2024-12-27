package cn.cpoet.ideas.iu.actions.patch.model;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.LinkedList;
import java.util.List;

/**
 * 生成补丁信息
 *
 * @author CPoet
 */
public class GenPatch {

    /** 补丁输出路径 */
    private VirtualFile outputPath;

    /** 补丁说明内容 */
    private StringBuilder desc = new StringBuilder();

    /** 补丁内容 */
    private List<GenPatchItem> items = new LinkedList<>();

    public VirtualFile getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(VirtualFile outputPath) {
        this.outputPath = outputPath;
    }

    public StringBuilder getDesc() {
        return desc;
    }

    public void setDesc(StringBuilder desc) {
        this.desc = desc;
    }

    public List<GenPatchItem> getItems() {
        return items;
    }

    public void setItems(List<GenPatchItem> items) {
        this.items = items;
    }
}
