package cn.cpoet.ideas.iu.actions.java.action;

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
public class AllGetNoDefaultIntentionAction extends AllGetIntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Generate all get";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        PsiJavaFile file = (PsiJavaFile) psiFile;
    }
}
