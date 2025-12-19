package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.EditorUtil;
import cn.cpoet.tool.util.UUIDUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * UUID生成器
 *
 * @author CPoet
 */
public class UUIDGenerateAction extends TextComponentEditorAction {

    public UUIDGenerateAction() {
        super(new Handler());
    }

    public static class Handler extends EditorWriteActionHandler {
        @Override
        public void executeWriteAction(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            EditorUtil.runWithCaret(editor, caret, c -> {
                String uuid = UUIDUtil.uuid32();
                int selectionStart = c.getSelectionStart();
                int selectionEnd = c.getSelectionEnd();
                editor.getDocument().replaceString(selectionStart, selectionEnd, uuid);
                c.setSelection(selectionStart, selectionStart + uuid.length());
                // 移动光标至尾部
                c.moveToOffset(selectionStart + uuid.length());
            });
        }
    }
}
