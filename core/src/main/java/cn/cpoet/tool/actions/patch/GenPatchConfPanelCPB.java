package cn.cpoet.tool.actions.patch;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public interface GenPatchConfPanelCPB {
    /**
     * @param project 项目
     * @param btn     {@link TextFieldWithBrowseButton}
     * @see GenPatchConfPanel#cpbOutputFolderTextField(TextFieldWithBrowseButton)
     */
    void cpbOutputFolderTextField(Project project, TextFieldWithBrowseButton btn);
}
