package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.Base64Util;

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
