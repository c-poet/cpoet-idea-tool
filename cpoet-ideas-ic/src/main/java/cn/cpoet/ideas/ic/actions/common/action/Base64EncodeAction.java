package cn.cpoet.ideas.ic.actions.common.action;

import cn.cpoet.ideas.ic.util.Base64Util;

/**
 * base64加码
 *
 * @author CPoet
 */
public class Base64EncodeAction extends AbstractSelectedTextAction {
    public Base64EncodeAction() {
        super(Base64Util::encode4str);
    }
}
