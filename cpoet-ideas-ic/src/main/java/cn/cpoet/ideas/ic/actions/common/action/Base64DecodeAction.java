package cn.cpoet.ideas.ic.actions.common.action;

import cn.cpoet.ideas.ic.util.Base64Util;

/**
 * base64解码
 *
 * @author CPoet
 */
public class Base64DecodeAction extends AbstractSelectedTextAction {
    public Base64DecodeAction() {
        super(Base64Util::decode2str);
    }
}
