package cn.cpoet.ideas.iu;

import cn.cpoet.ideas.ic.i18n.I18nService;

/**
 * @author CPoet
 */
public class IUI18nService implements I18nService {
    @Override
    public String[] getPrefix() {
        return new String[]{"messages/cpoet-ideas-iu"};
    }
}
