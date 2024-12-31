package cn.cpoet.ideas.util;

/**
 * 字符串处理工具
 *
 * @author CPoet
 */
public abstract class StrUtil {

    private static final char CHAR_UNDERLINE = '_';

    private StrUtil() {
    }

    /**
     * 驼峰转下划线
     *
     * @param text 文本
     * @return 转换结果
     */
    public static String toUnderline(String text) {
        if (text == null || text.length() <= 1) {
            return text;
        }
        char[] charArray = text.toCharArray();
        StringBuilder sb = new StringBuilder(text.length()).append(charArray[0]);
        for (int i = 1; i < charArray.length; ++i) {
            if (charArray[i - 1] != CHAR_UNDERLINE
                    && !Character.isUpperCase(charArray[i - 1])
                    && Character.isUpperCase(charArray[i])) {
                sb.append(CHAR_UNDERLINE);
            }
            sb.append(charArray[i]);
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param text 文本
     * @return 转换结果
     */
    public static String toCamel(String text) {
        if (text == null || text.length() <= 1) {
            return text;
        }
        char[] charArray = text.toCharArray();
        StringBuilder sb = new StringBuilder(text.length());
        int i = 0;
        while (i < charArray.length && charArray[i] == CHAR_UNDERLINE) {
            sb.append(charArray[i]);
            ++i;
        }
        sb.append(charArray[i++]);
        while (i < charArray.length) {
            if (charArray[i] == CHAR_UNDERLINE && (i == charArray.length - 1 || charArray[i + 1] == CHAR_UNDERLINE)) {
                sb.append(CHAR_UNDERLINE);
            } else if (charArray[i] != CHAR_UNDERLINE) {
                sb.append(charArray[i - 1] == CHAR_UNDERLINE ? Character.toUpperCase(charArray[i]) : charArray[i]);
            }
            ++i;
        }
        return sb.toString();
    }
}
