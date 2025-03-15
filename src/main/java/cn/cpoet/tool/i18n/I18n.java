package cn.cpoet.tool.i18n;

import cn.cpoet.tool.setting.Setting;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ServiceLoader;

/**
 * 国际化工具
 *
 * @author CPoet
 */
public abstract class I18n {

    private static final String I18N_FILE_PREFIX = "messages/cpoet-tool";

    private static I18nChain i18NChain;

    static {
        initLocale();
    }

    private I18n() {
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
        return i18NChain.getMessage(name, defaultMessage);
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
        i18NChain = new I18nChain(null, I18N_FILE_PREFIX);
        ServiceLoader<I18nService> loader = ServiceLoader.load(I18nService.class, I18n.class.getClassLoader());
        for (I18nService next : loader) {
            String[] prefix = next.getPrefix();
            if (prefix != null) {
                for (String pre : prefix) {
                    i18NChain = new I18nChain(i18NChain, pre);
                }
            }
        }
        updateLocale();
    }

    public static void updateLocale() {
        Locale locale = new Locale(getLanguage());
        i18NChain.setLocale(locale);
    }
}
