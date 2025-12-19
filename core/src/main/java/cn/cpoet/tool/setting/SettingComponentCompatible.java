package cn.cpoet.tool.setting;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public interface SettingComponentCompatible {
    /**
     * @param btn {@link TextFieldWithBrowseButton}
     * @see SettingComponent#compatiblePatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton)
     */
    void compatiblePatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton btn);
}
