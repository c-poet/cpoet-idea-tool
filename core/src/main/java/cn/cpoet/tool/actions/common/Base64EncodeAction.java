package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.constant.TextEncodeEnum;
import cn.cpoet.tool.setting.Setting;
import cn.cpoet.tool.util.Base64Util;
import cn.cpoet.tool.util.I18nUtil;

import java.nio.charset.Charset;

/**
 * base64加码
 *
 * @author CPoet
 */
public class Base64EncodeAction extends AbstractSelectedTextAction {
    public Base64EncodeAction() {
        super(I18nUtil.td("actions.common.Base64EncodeAction.title"),
                I18nUtil.td("actions.common.Base64EncodeAction.description"), text -> {
                    Charset charset = TextEncodeEnum.ofCode(Setting.getInstance().getState().textEncode).getCharset();
                    return Base64Util.encode4str(text, charset);
                });
    }
}
