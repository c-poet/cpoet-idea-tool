package cn.cpoet.tool.actions.database;

import com.intellij.database.datagrid.*;
import com.intellij.database.util.JdbcUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAwareAction;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author CPoet
 */
public class CopySingleColJoinAction extends DumbAwareAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        DataGrid grid = DataGridUtil.getDataGrid(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(grid != null && grid.getSelectionModel().getSelectedColumnCount() == 1);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DataGrid grid = Objects.requireNonNull(DataGridUtil.getDataGrid(anActionEvent.getDataContext()));
        SelectionModel<GridRow, GridColumn> selectionModel = grid.getSelectionModel();
        List<GridRow> rows = grid.getDataHookup().getDataModel().getRows(selectionModel.getSelectedRows());
        GridColumn column = Objects.requireNonNull(grid.getDataHookup().getDataModel().getColumn(selectionModel.getSelectedColumns().first()));
        StringJoiner joiner = JdbcUtil.hasScaleAndPrecision(column.getType()) ? new StringJoiner(", ") : new StringJoiner("', '", "'", "'");
        for (GridRow row : rows) {
            String val = Objects.toString(column.getValue(row), null);
            if (StringUtils.isNotEmpty(val)) {
                joiner.add(val);
            }
        }
        if (joiner.length() > 0) {
            CopyPasteManager.getInstance().setContents(new StringSelection(joiner.toString()));
        }
    }
}
