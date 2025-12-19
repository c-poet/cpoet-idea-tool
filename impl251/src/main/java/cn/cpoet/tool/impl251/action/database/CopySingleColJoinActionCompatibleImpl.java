package cn.cpoet.tool.impl251.action.database;

import cn.cpoet.tool.actions.database.CopySingleColJoinActionCompatible;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class CopySingleColJoinActionCompatibleImpl implements CopySingleColJoinActionCompatible {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // DataGrid grid = DataGridUtil.getDataGrid(e.getDataContext());
        // e.getPresentation().setEnabledAndVisible(grid != null && grid.getSelectionModel().getSelectedColumnCount() == 1);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }
}
