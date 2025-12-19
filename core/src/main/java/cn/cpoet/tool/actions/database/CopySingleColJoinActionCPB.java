package cn.cpoet.tool.actions.database;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public interface CopySingleColJoinActionCPB {
    /**
     * @param e {@link AnActionEvent}
     * @see CopySingleColJoinAction#update(AnActionEvent)
     */
    void update(@NotNull AnActionEvent e);

    /**
     * @param anActionEvent {@link AnActionEvent}
     * @see CopySingleColJoinAction#actionPerformed(AnActionEvent)
     */
    void actionPerformed(@NotNull AnActionEvent anActionEvent);
}
