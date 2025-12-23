package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.Base64Util;
import cn.cpoet.tool.util.I18nUtil;

/**
 * base64解码
 *
 * @author CPoet
 */
public class Base64DecodeAction extends AbstractSelectedTextAction {
    public Base64DecodeAction() {
        super(I18nUtil.td("actions.common.Base64DecodeAction.title"),
                I18nUtil.td("actions.common.Base64DecodeAction.description"), Base64Util::decode2str);
    }
}
