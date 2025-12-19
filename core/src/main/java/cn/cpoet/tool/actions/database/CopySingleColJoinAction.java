package cn.cpoet.tool.actions.database;

import cn.cpoet.tool.compatible.CompatibleService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class CopySingleColJoinAction extends DumbAwareAction {

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
