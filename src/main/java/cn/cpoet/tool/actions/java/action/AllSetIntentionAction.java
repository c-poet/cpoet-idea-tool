package cn.cpoet.tool.actions.java.action;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public class AllSetIntentionAction extends AllGetIntentionAction {

    @Override
    public @IntentionName @NotNull String getText() {
        return "Generate all set";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        System.out.println("111111111111111");
    }
}
