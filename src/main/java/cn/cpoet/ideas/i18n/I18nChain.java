package cn.cpoet.ideas.i18n;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author CPoet
 */
final class I18nChain {

    /** 父级 */
    private final I18nChain parent;

    /** 文件前缀 */
    private final String filePrefix;

    /** 资源 */
    private ResourceBundle resourceBundle;

    public I18nChain(I18nChain parent, String filePrefix) {
        this.parent = parent;
        this.filePrefix = filePrefix;
    }


    public I18nChain getParent() {
        return parent;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    /**
     * 获取国际化内容
     *
     * @param name           名称
     * @param defaultMessage 默认值
     * @return 值
     */
    @NotNull
    public String getMessage(String name, @NotNull String defaultMessage) {
        String message = doGetMessage(name);
        return StringUtils.isEmpty(message) ? defaultMessage : message;
    }

    private String doGetMessage(String name) {
        try {
            return resourceBundle.getString(name);
        } catch (Exception ignored) {
        }
        return parent == null ? null : parent.doGetMessage(name);
    }

    public void setLocale(Locale locale) {
        if (parent != null) {
            parent.setLocale(locale);
        }
        if (resourceBundle != null && Objects.equals(resourceBundle.getLocale(), locale)) {
            return;
        }
        resourceBundle = ResourceBundle.getBundle(filePrefix, locale);
    }
}
