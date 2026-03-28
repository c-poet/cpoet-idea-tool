package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.constant.TextEncodeEnum;
import cn.cpoet.tool.setting.Setting;
import cn.cpoet.tool.util.Base64Util;
import cn.cpoet.tool.util.I18nUtil;

import java.nio.charset.Charset;

/**
 * base64解码
 *
 * @author CPoet
 */
public class Base64DecodeAction extends AbstractSelectedTextAction {
    public Base64DecodeAction() {
        super(I18nUtil.td("actions.common.Base64DecodeAction.title"),
                I18nUtil.td("actions.common.Base64DecodeAction.description"), text -> {
                    Charset charset = TextEncodeEnum.ofCode(Setting.getInstance().getState().textEncode).getCharset();
                    return Base64Util.decode2str(text, charset);
                });
    }
}
