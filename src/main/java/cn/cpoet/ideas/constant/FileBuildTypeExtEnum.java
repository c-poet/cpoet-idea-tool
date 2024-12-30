package cn.cpoet.ideas.constant;

/**
 * 常用文件编译类型后缀枚举
 *
 * @author CPoet
 */
public enum FileBuildTypeExtEnum {

    JAVA("java", "class"),

    KOTLIN("kt", "class"),
    ;

    /** 源文件后缀 */
    private final String sourceExt;

    /** 输出文件后缀 */
    private final String buildExt;

    public String getSourceExt() {
        return sourceExt;
    }

    public String getBuildExt() {
        return buildExt;
    }

    FileBuildTypeExtEnum(final String sourceExt, final String buildExt) {
        this.sourceExt = sourceExt;
        this.buildExt = buildExt;
    }

    public static String findBuildExt(String sourceExt) {
        for (FileBuildTypeExtEnum item : values()) {
            if (item.sourceExt.equals(sourceExt)) {
                return item.buildExt;
            }
        }
        return null;
    }

    public static String findSourceExt(String buildExt) {
        for (FileBuildTypeExtEnum item : values()) {
            if (item.buildExt.equals(buildExt)) {
                return item.sourceExt;
            }
        }
        return null;
    }
}