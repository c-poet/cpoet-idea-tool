package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.HashUtil;
import cn.cpoet.tool.util.I18nUtil;

/**
 * @author CPoet
 */
public class Sha1Action extends AbstractSelectedTextAction {
    public Sha1Action() {
        super(I18nUtil.td("actions.common.Sha1Action.title"),
                I18nUtil.td("actions.common.Sha1Action.description"), HashUtil::sha1);
    }
}
