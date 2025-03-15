package cn.cpoet.tool.actions.common.action;

import cn.cpoet.tool.util.StrUtil;

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
