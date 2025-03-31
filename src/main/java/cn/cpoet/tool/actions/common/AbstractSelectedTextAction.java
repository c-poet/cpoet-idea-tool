package cn.cpoet.tool.actions.common;

import cn.cpoet.tool.util.EditorUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author CPoet
 */
public abstract class AbstractSelectedTextAction extends TextComponentEditorAction {

    public AbstractSelectedTextAction(Function<String, String> func) {
        super(new Handler(func));
    }

    public static class Handler extends EditorWriteActionHandler {

        private final Function<String, String> func;

        public Handler(Function<String, String> func) {
            this.func = func;
        }

        @Override
        protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
            return caret.hasSelection();
        }

        @Override
        public void executeWriteAction(final @NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
            EditorUtil.runWithCaret(editor, caret, c -> {
                int selectionStart = c.getSelectionStart();
                int selectionEnd = c.getSelectionEnd();
                String text = editor.getDocument().getText(new TextRange(selectionStart, selectionEnd));
                String processText = getProcessText(text);
                if (!text.equals(processText)) {
                    editor.getDocument().replaceString(selectionStart, selectionEnd, processText);
                    if (!c.hasSelection()) {
                        c.setSelection(selectionStart, selectionEnd + processText.length() - text.length());
                    }
                    c.moveToOffset(selectionStart + processText.length());
                }
            });
        }

        protected String getProcessText(String originText) {
            try {
                return func.apply(originText);
            } catch (Exception ignored) {
            }
            return originText;
        }
    }
}
