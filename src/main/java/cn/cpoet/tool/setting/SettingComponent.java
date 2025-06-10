package cn.cpoet.tool.setting;

import cn.cpoet.tool.component.CustomComboBox;
import cn.cpoet.tool.constant.LanguageEnum;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * 插件配置组件
 *
 * @author CPoet
 */
public class SettingComponent {

    private final JPanel mainPanel;

    private final ComboBox<LanguageEnum> selectLanguageComboBox;
    private final TextFieldWithBrowseButton patchAssistant2JTextFieldWithBtn;

    public SettingComponent() {
        selectLanguageComboBox = buildSelectLanguageComboBox();
        patchAssistant2JTextFieldWithBtn = new TextFieldWithBrowseButton();
        patchAssistant2JTextFieldWithBtn.addBrowseFolderListener(I18nUtil.t("settings.PatchAssistant2J.path")
                , null, null, FileChooserDescriptorFactory.createSingleFileDescriptor("exe"));
        mainPanel = FormBuilder.createFormBuilder().setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.SelectLanguage.label"), selectLanguageComboBox)
                .addLabeledComponent(I18nUtil.t("settings.PatchAssistant2J.label"), patchAssistant2JTextFieldWithBtn)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private ComboBox<LanguageEnum> buildSelectLanguageComboBox() {
        CustomComboBox<LanguageEnum> comboBox = new CustomComboBox<>();
        comboBox.customText(LanguageEnum::getName);
        for (LanguageEnum value : LanguageEnum.values()) {
            comboBox.addItem(value);
        }
        return comboBox;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public LanguageEnum getLanguage() {
        return (LanguageEnum) selectLanguageComboBox.getSelectedItem();
    }

    public void setLanguage(LanguageEnum language) {
        selectLanguageComboBox.setSelectedItem(language);
    }

    public String getPatchAssistant2JPath() {
        return patchAssistant2JTextFieldWithBtn.getText();
    }

    public void setPatchAssistant2JPath(String patchAssistant2JPath) {
        patchAssistant2JTextFieldWithBtn.setText(patchAssistant2JPath);
    }
}
