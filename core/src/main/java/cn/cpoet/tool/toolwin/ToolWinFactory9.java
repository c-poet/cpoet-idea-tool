package cn.cpoet.tool.toolwin;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author CPoet
 */
public interface ToolWinFactory9 {
    /**
     * Since 2023.3.8
     *
     * @see #isApplicable(Project)
     */
    boolean isApplicableAsync(@NotNull Project project);

    /**
     * Deprecated method in 2023.3.8
     */
    boolean isApplicable(@NotNull Project project);
}
