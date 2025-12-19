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
                .instance(CopySingleColJoinActionCompatible.class)
                .update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        CompatibleService.getInstance()
                .instance(CopySingleColJoinActionCompatible.class)
                .actionPerformed(anActionEvent);
    }
}
