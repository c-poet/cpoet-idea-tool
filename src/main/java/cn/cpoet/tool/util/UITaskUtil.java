package cn.cpoet.tool.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author CPoet
 */
public abstract class UITaskUtil {
    private UITaskUtil() {
    }

    public static boolean isUi() {
        return ApplicationManager.getApplication().isDispatchThread();
    }

    public static void runUI(Runnable runnable) {
        runUI(runnable, ModalityState.defaultModalityState());
    }

    public static void runUI(Runnable runnable, ModalityState state) {
        if (isUi()) {
            runnable.run();
        } else {
            ApplicationManager.getApplication().invokeLater(runnable, state);
        }
    }

    public static void runNotUi(Runnable runnable) {
        if (isUi()) {
            CompletableFuture.runAsync(runnable);
        } else {
            runnable.run();
        }
    }

    public static void runProgress(Project project, String title, Consumer<ProgressIndicator> runnable) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, title) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                runnable.accept(progressIndicator);
            }
        });
    }
}
