package cn.cpoet.tool.impl223.actions.patch;

import cn.cpoet.tool.actions.patch.GenPatchConfPanelCompatible;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class GenPatchConfPanelCompatibleImpl implements GenPatchConfPanelCompatible {

    @Override
    public void compatibleOutputFolderTextField(Project project, TextFieldWithBrowseButton btn) {
        btn.addBrowseFolderListener(I18nUtil.t("actions.patch.GenPatchPackageAction.config.outputFolder"), null
                , project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }
}
