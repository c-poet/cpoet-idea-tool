package cn.cpoet.tool.impl243.actions.patch;

import cn.cpoet.tool.actions.patch.GenPatchConfPanelCompatible;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class GenPatchConfPanelCompatibleImpl implements GenPatchConfPanelCompatible {

    @Override
    public void compatibleOutputFolderTextField(Project project, TextFieldWithBrowseButton btn) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle(I18nUtil.t("actions.patch.GenPatchPackageAction.config.outputFolder"));
        btn.addBrowseFolderListener(project, descriptor);
    }
}
