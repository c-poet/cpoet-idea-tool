package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.HashUtil;
import cn.cpoet.tool.util.I18nUtil;

/**
 * @author CPoet
 */
public class Md5Action extends AbstractSelectedTextAction {

    public Md5Action() {
        super(I18nUtil.td("actions.common.Md5Action.title"),
                I18nUtil.td("actions.common.Md5Action.description"), HashUtil::md5);
    }
}
