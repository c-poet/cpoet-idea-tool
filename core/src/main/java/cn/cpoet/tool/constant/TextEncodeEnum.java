package cn.cpoet.tool.constant;

import cn.cpoet.tool.util.I18nUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符编码枚举
 *
 * @author CPoet
 */
public enum TextEncodeEnum {

    /**
     * UTF-8
     */
    UTF_8("UTF-8", StandardCharsets.UTF_8),

    /**
     * GBK
     */
    GBK("GBK", Charset.forName("GBK"));

    private final String code;
    private final Charset charset;

    TextEncodeEnum(final String code, final Charset charset) {
        this.code = code;
        this.charset = charset;
    }

    public String getName() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public Charset getCharset() {
        return charset;
    }

    public static TextEncodeEnum ofCode(String code) {
        for (TextEncodeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return UTF_8;
    }
}
