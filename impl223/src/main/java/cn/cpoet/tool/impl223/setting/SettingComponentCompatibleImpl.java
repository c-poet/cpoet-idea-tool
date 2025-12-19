package cn.cpoet.tool.impl223.setting;

import cn.cpoet.tool.setting.SettingComponentCompatible;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class SettingComponentCompatibleImpl implements SettingComponentCompatible {

    @Override
    public void compatiblePatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton btn) {
        btn.addBrowseFolderListener(I18nUtil.t("settings.PatchAssistant2J.path")
                , null, null, FileChooserDescriptorFactory.createSingleFileDescriptor("exe"));
    }
}
