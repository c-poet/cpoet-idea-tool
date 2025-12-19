package cn.cpoet.tool.impl243.setting;

import cn.cpoet.tool.setting.SettingComponentCPB;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class SettingComponentCPBImpl implements SettingComponentCPB {

    @Override
    public void cpbPatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton textFieldWithBrowseButton) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("exe").withTitle(I18nUtil.t("settings.PatchAssistant2J.path"));
        textFieldWithBrowseButton.addBrowseFolderListener(null, descriptor);
    }
}
