package cn.cpoet.ideas.ic.actions.common.action;

import cn.cpoet.ideas.ic.util.StrUtil;

/**
 * 转换成下划线
 *
 * @author CPoet
 */
public class ConvertToUnderlineAction extends AbstractSelectedTextAction {
    public ConvertToUnderlineAction() {
        super(StrUtil::toUnderline);
    }
}
