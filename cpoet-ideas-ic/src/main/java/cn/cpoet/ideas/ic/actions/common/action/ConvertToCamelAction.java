package cn.cpoet.ideas.ic.actions.common.action;

import cn.cpoet.ideas.ic.util.StrUtil;

/**
 * 转换成驼峰
 *
 * @author CPoet
 */
public class ConvertToCamelAction extends AbstractSelectedTextAction {
    public ConvertToCamelAction() {
        super(StrUtil::toCamel);
    }
}
