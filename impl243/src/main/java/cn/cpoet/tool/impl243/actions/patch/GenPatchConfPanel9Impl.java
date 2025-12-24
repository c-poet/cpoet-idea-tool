package cn.cpoet.tool.impl243.actions.patch;

import cn.cpoet.tool.actions.patch.GenPatchConfPanel9;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class GenPatchConfPanel9Impl implements GenPatchConfPanel9 {

    @Override
    public void cpbOutputFolderTextField(Project project, TextFieldWithBrowseButton textFieldWithBrowseButton) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.config.outputFolder"));
        textFieldWithBrowseButton.addBrowseFolderListener(project, descriptor);
    }
}
