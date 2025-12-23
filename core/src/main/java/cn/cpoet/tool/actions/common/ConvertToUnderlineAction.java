package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.I18nUtil;
import cn.cpoet.tool.util.StrUtil;

/**
 * 转换成下划线
 *
 * @author CPoet
 */
public class ConvertToUnderlineAction extends AbstractSelectedTextAction {
    public ConvertToUnderlineAction() {
        super(I18nUtil.td("actions.common.ConvertToUnderlineAction.title"),
                I18nUtil.td("actions.common.ConvertToUnderlineAction.description"),
                StrUtil::toUnderline);
    }
}
