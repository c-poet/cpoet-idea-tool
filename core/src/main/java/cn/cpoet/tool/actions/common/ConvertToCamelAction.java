package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.I18nUtil;
import cn.cpoet.tool.util.StrUtil;

/**
 * 转换成驼峰
 *
 * @author CPoet
 */
public class ConvertToCamelAction extends AbstractSelectedTextAction {
    public ConvertToCamelAction() {
        super(I18nUtil.td("actions.common.ConvertToCamelAction.title"),
                I18nUtil.td("actions.common.ConvertToCamelAction.description"), StrUtil::toCamel);
    }
}
