package cn.cpoet.tool.util;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.Editor;

/**
 * 编辑器工具
 *
 * @author CPoet
 */
public abstract class EditorUtil {
    private EditorUtil() {
    }

    /**
     * 编辑器获取光标的情况下执行
     *
     * @param editor 编辑器
     * @param caret  插入点
     * @param action 操作
     */
    public static void runWithCaret(Editor editor, Caret caret, CaretAction action) {
        if (caret != null) {
            action.perform(caret);
        } else {
            editor.getCaretModel().runForEachCaret(action);
        }
    }
}
