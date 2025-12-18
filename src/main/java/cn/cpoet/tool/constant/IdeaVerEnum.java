package cn.cpoet.tool.constant;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.BuildNumber;

/**
 * Idea版本记录
 * <p>可用于版本兼容处理</p>
 *
 * @author CPoet
 */
public enum IdeaVerEnum {
    /** 2024.3 */
    V243("243");

    private final String code;

    IdeaVerEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public BuildNumber getBuildNumber() {
        return BuildNumber.fromString(code);
    }

    /**
     * 判断当前Idea版本是否在指定范围内
     *
     * @return 满足当前版本
     */
    public boolean isNewer() {
        ApplicationInfo info = ApplicationInfo.getInstance();
        BuildNumber buildNumber = getBuildNumber();
        if (buildNumber == null) {
            return false;
        }
        return info.getBuild().compareTo(buildNumber) >= 0;
    }
}
