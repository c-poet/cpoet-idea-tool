package cn.cpoet.ideas.actions.java.action;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class AllGetIntentionAction extends AbstractIntentionAction {

    @Override
    public @IntentionName @NotNull String getText() {
        return "Generate all get";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        // 判断是否是Java文件
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        // 获取当前光标所在的关键字
        return super.isAvailable(project, editor, file);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {

    }
}
