package cn.cpoet.tool.actions.patch.component;

import cn.cpoet.tool.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.tool.actions.patch.setting.GenPatchSetting;
import cn.cpoet.tool.component.CustomComboBox;
import cn.cpoet.tool.component.ScrollVPane;
import cn.cpoet.tool.component.TitledPanel;
import cn.cpoet.tool.i18n.I18n;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.DocumentEvent;
import java.awt.event.ItemEvent;

/**
 * 生成补丁配置面板
 *
 * @author CPoet
 */
public class GenPatchConfPane extends ScrollVPane {

    private final Project project;
    private String fileNamePrefix;
    private JBTextField fileNameField;
    private final GenPatchPanel parent;

    public GenPatchConfPane(Project project, GenPatchPanel parent) {
        this.parent = parent;
        this.project = project;
        setBorder(JBUI.Borders.empty());
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        buildGeneral(setting);
        buildBeforeGenerate(setting);
        buildAfterGenerate(setting);
    }

    public void buildGeneral(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        FormBuilder formBuilder = createFormBuilder();
        fileNameField = new JBTextField();
        fileNamePrefix = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd") + "_";
        if (StringUtils.isNotBlank(state.lastFileName)) {
            if (StringUtils.isNotBlank(state.lastFileNamePrefix) && state.lastFileName.startsWith(state.lastFileNamePrefix)) {
                fileNameField.setText(fileNamePrefix + state.lastFileName.substring(state.lastFileNamePrefix.length()));
            } else {
                fileNameField.setText(state.lastFileName);
            }
        } else {
            fileNameField.setText(fileNamePrefix + project.getName());
        }
        fileNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                parent.updateBtnStatus();
            }
        });
        formBuilder.addLabeledComponent(I18n.t("actions.patch.GenPatchPackageAction.config.fileName"), fileNameField);

        // 选择输出的目录
        TextFieldWithBrowseButton outputFolderTextField = new TextFieldWithBrowseButton();
        outputFolderTextField.addBrowseFolderListener(I18n.t("actions.patch.GenPatchPackageAction.config.outputFolder")
                , null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
        outputFolderTextField.setText(state.outputFolder);
        outputFolderTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                setting.getState().outputFolder = outputFolderTextField.getText();
                parent.updateBtnStatus();
            }
        });
        formBuilder.addLabeledComponent(I18n.t("actions.patch.GenPatchPackageAction.config.outputFolder"), outputFolderTextField);

        JBCheckBox coverCheckBox = new JBCheckBox(I18n.t("actions.patch.GenPatchPackageAction.config.cover"), state.cover);
        coverCheckBox.addActionListener(e -> setting.getState().cover = !setting.getState().cover);
        formBuilder.addComponent(coverCheckBox);

        // 是否编译
        JBCheckBox compressCheckBox = new JBCheckBox(I18n.t("actions.patch.GenPatchPackageAction.config.compress"), state.compress);
        compressCheckBox.addActionListener(e -> setting.getState().compress = !setting.getState().compress);
        formBuilder.addComponent(compressCheckBox);

        // 包含路径
        JBCheckBox includePathCheckBox = new JBCheckBox(I18n.t("actions.patch.GenPatchPackageAction.config.includePath"), state.includePath);
        includePathCheckBox.addActionListener(e -> setting.getState().includePath = !setting.getState().includePath);
        formBuilder.addComponent(includePathCheckBox);

        TitledPanel titledPanel = new TitledPanel(I18n.t("actions.patch.GenPatchPackageAction.config.generalTitle"));
        titledPanel.add(formBuilder.getPanel());
        add2View(titledPanel);
    }

    public void buildBeforeGenerate(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        FormBuilder formBuilder = createFormBuilder();
        CustomComboBox<GenPatchBuildTypeEnum> buildTypeComboBox = new CustomComboBox<>();
        for (GenPatchBuildTypeEnum genPatchBuildTypeEnum : GenPatchBuildTypeEnum.values()) {
            buildTypeComboBox.addItem(genPatchBuildTypeEnum);
        }
        buildTypeComboBox.customText(GenPatchBuildTypeEnum::getName);
        buildTypeComboBox.setSelectedItem(GenPatchBuildTypeEnum.ofCode(state.buildType));
        buildTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                GenPatchBuildTypeEnum buildTypeEnum = (GenPatchBuildTypeEnum) e.getItem();
                setting.getState().buildType = buildTypeEnum.getCode();
            }
        });
        formBuilder.addLabeledComponent(I18n.t("actions.patch.GenPatchPackageAction.config.buildType.label"), buildTypeComboBox);
        TitledPanel configTitledPanel = new TitledPanel(I18n.t("actions.patch.GenPatchPackageAction.config.beforeTitle"));
        configTitledPanel.add(formBuilder.getPanel());
        add2View(configTitledPanel);
    }

    public void buildAfterGenerate(GenPatchSetting setting) {
        FormBuilder formBuilder = createFormBuilder();
        JBCheckBox openOutputFolderCheckBox = new JBCheckBox(I18n.t("actions.patch.GenPatchPackageAction.config.openOutputFolder"), setting.getState().openOutputFolder);
        openOutputFolderCheckBox.addActionListener((event) -> setting.getState().openOutputFolder = !setting.getState().openOutputFolder);
        formBuilder.addComponent(openOutputFolderCheckBox);
        TitledPanel titledPanel = new TitledPanel(I18n.t("actions.patch.GenPatchPackageAction.config.afterTitle"));
        titledPanel.add(formBuilder.getPanel());
        add2View(titledPanel);
    }

    protected FormBuilder createFormBuilder() {
        return FormBuilder.createFormBuilder().setFormLeftIndent(20);
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public String getFileName() {
        return fileNameField == null ? null : fileNameField.getText();
    }
}
