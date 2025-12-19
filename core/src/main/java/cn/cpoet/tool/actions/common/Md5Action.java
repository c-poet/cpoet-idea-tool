package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.HashUtil;

/**
 * @author CPoet
 */
public class Md5Action extends AbstractSelectedTextAction {

    public Md5Action() {
        super(HashUtil::md5);
    }
}
