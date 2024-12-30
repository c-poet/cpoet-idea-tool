package cn.cpoet.ideas.iu.actions.patch.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 生成补丁信息
 *
 * @author CPoet
 */
public class GenPatch {

    /** 补丁输出路径 */
    private String outputFolder;

    /** 补丁文件名 */
    private String fileName;

    /** 补丁说明内容 */
    private StringBuilder desc = new StringBuilder();

    /** 补丁内容 */
    private List<GenPatchItem> items = new LinkedList<>();

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
