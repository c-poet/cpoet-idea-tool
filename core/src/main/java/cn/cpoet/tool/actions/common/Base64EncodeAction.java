package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.Base64Util;
import cn.cpoet.tool.util.I18nUtil;

/**
 * base64加码
 *
 * @author CPoet
 */
public class Base64EncodeAction extends AbstractSelectedTextAction {
    public Base64EncodeAction() {
        super(I18nUtil.td("actions.common.Base64EncodeAction.title"),
                I18nUtil.td("actions.common.Base64EncodeAction.description"), Base64Util::encode4str);
    }
}
