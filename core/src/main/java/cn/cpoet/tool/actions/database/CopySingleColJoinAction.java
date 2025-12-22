package cn.cpoet.tool.actions.database;

import cn.cpoet.tool.compatible.CompatibleService;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class CopySingleColJoinAction extends DumbAwareAction {

    protected CopySingleColJoinAction() {
        super(I18nUtil.td("actions.database.CopySingleColJoinAction.title"));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        CompatibleService.getInstance()
                .instance(CopySingleColJoinActionCPB.class)
                .update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        CompatibleService.getInstance()
                .instance(CopySingleColJoinActionCPB.class)
                .actionPerformed(anActionEvent);
    }
}
