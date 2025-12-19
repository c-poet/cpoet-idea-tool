package cn.cpoet.tool.actions.patch;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public interface GenPatchConfPanelCompatible {
    /**
     * @param project 项目
     * @param btn     {@link TextFieldWithBrowseButton}
     * @see GenPatchConfPanel#compatibleOutputFolderTextField(TextFieldWithBrowseButton)
     */
    void compatibleOutputFolderTextField(Project project, TextFieldWithBrowseButton btn);
}
