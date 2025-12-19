package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.HashUtil;

/**
 * @author CPoet
 */
public class Sha1Action extends AbstractSelectedTextAction {
    public Sha1Action() {
        super(HashUtil::sha1);
    }
}
