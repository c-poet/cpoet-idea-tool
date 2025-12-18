package cn.cpoet.tool.util;

import cn.cpoet.tool.setting.Setting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * 国际化工具
 *
 * @author CPoet
 */
public abstract class I18nUtil {

    private static final String I18N_FILE_PREFIX = "messages/cpoet-tool";

    /** I18n资源 */
    private static ResourceBundle resourceBundle;

    static {
        initLocale();
    }

    private I18nUtil() {
    }

    /**
     * 获取国际化内容
     *
     * @param name 名称
     * @return 值
     */
    @NotNull
    public static String t(String name) {
        return t(name, "");
    }

    /**
     * 获取国际化内容
     *
     * @param name           名称
     * @param defaultMessage 默认值
     * @return 值
     */
    @NotNull
    public static String t(String name, @NotNull String defaultMessage) {
        try {
            return resourceBundle.getString(name);
        } catch (Exception ignored) {
        }
        return defaultMessage;
    }

    /**
     * 获取国际化内容
     *
     * @param name   名称
     * @param params 参数列表
     * @return 值
     */
    @NotNull
    public static String tr(String name, Object... params) {
        String message = t(name);
        return StringUtils.isBlank(message) ? message : String.format(message, params);
    }

    public static String getLanguage() {
        Setting.State state = Setting.getInstance().getState();
        return state.language;
    }

    private static void initLocale() {
        updateLocale();
    }

    public static void updateLocale() {
        Locale locale = new Locale(getLanguage());
        if (resourceBundle != null && Objects.equals(resourceBundle.getLocale(), locale)) {
            return;
        }
        resourceBundle = ResourceBundle.getBundle(I18N_FILE_PREFIX, locale);
    }
}
