package cn.cpoet.tool.actions.patch;

import cn.cpoet.tool.component.SimpleVPanel;
import cn.cpoet.tool.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.DocumentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 替换补丁面板
 *
 * @author CPoet
 */
public class ReplacePatchPanel extends SimpleVPanel {

    private String patchPath;
    private final ReplacePatchSetting setting;

    public ReplacePatchPanel(Project project, String patchPath) {
        this.patchPath = patchPath;
        this.setting = ReplacePatchSetting.getInstance(project);
        setPreferredSize(new JBDimension(setting.getState().width, setting.getState().height));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setting.getState().width = getWidth();
                setting.getState().height = getHeight();
            }
        });
        ReplacePatchSetting.State state = setting.getState();
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        TextFieldWithBrowseButton appPackagePathTextField = new TextFieldWithBrowseButton();
        FileChooserDescriptor appPackageChooserDesc = new FileChooserDescriptor(true, false, true, false, false, false);
        appPackageChooserDesc.withFileFilter(file -> "jar".equals(file.getExtension()) || "war".equals(file.getExtension()));
        appPackagePathTextField.addBrowseFolderListener(I18nUtil.t("actions.patch.ReplacePatchAction.appPackage")
                , null, project, appPackageChooserDesc);
        appPackagePathTextField.setText(state.appPackagePath);
        appPackagePathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                setting.getState().appPackagePath = appPackagePathTextField.getText();
            }
        });
        formBuilder.addLabeledComponent(I18nUtil.t("actions.patch.ReplacePatchAction.appPackage"), appPackagePathTextField);
        TextFieldWithBrowseButton patchPathTextField = new TextFieldWithBrowseButton();
        FileChooserDescriptor patchPathFileChooserDesc = new FileChooserDescriptor(true, true, true, false, false, false);
        patchPathFileChooserDesc.withFileFilter(file -> file.isDirectory() || "zip".equals(file.getExtension()) || "rar".equals(file.getExtension()));
        patchPathTextField.addBrowseFolderListener(I18nUtil.t("actions.patch.ReplacePatchAction.patchPath")
                , null, project, patchPathFileChooserDesc);
        patchPathTextField.setText(this.patchPath);
        patchPathTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                ReplacePatchPanel.this.patchPath = patchPathTextField.getText();
            }
        });
        formBuilder.addLabeledComponent(I18nUtil.t("actions.patch.ReplacePatchAction.patchPath"), patchPathTextField);
        this.add(formBuilder.getPanel());
    }

    public void handleReplace() {

    }
}
