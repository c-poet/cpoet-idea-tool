package cn.cpoet.tool.actions.common.action;

import cn.cpoet.tool.util.StrUtil;

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
