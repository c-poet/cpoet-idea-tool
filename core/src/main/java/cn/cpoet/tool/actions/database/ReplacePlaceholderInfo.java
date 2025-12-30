package cn.cpoet.tool.actions.database;

import java.util.Map;

/**
 * 替换占位符信息
 *
 * @author CPoet
 */
public class ReplacePlaceholderInfo {
    private int startIndex;

    private int endIndex;

    private Map<String, Object> params;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
