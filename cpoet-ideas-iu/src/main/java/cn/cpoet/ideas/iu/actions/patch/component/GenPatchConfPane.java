package cn.cpoet.ideas.iu.actions.patch.component;

import cn.cpoet.ideas.iu.actions.patch.constant.GenPatchBuildTypeEnum;
import cn.cpoet.ideas.ic.component.CustomComboBox;
import cn.cpoet.ideas.ic.component.ScrollVPane;
import cn.cpoet.ideas.ic.component.TitledPanel;
import cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import java.awt.event.ItemEvent;

/**
 * 生成补丁配置面板
 *
 * @author CPoet
 */
public class GenPatchConfPane extends ScrollVPane {

    public GenPatchConfPane(Project project) {
        setBorder(JBUI.Borders.empty());
        cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting setting = cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting.getInstance(project);
        buildBeforeGenerate(setting);
        buildAfterGenerate(setting);
    }

    public void buildBeforeGenerate(cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting setting) {
        cn.cpoet.ideas.iu.actions.patch.setting.GenPatchSetting.State state = setting.getState();
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
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
        formBuilder.addLabeledComponent("Build type", buildTypeComboBox);
        TitledPanel configTitledPanel = new TitledPanel("Before Generate");
        configTitledPanel.add(formBuilder.getPanel());
        add2View(configTitledPanel);
    }

    public void buildAfterGenerate(GenPatchSetting setting) {
        TitledPanel titledPanel = new TitledPanel("After Generate");
        JBCheckBox cleanBuildFileCheckBox = new JBCheckBox("Clean build file", setting.getState().cleanBuildFile);
        titledPanel.add(cleanBuildFileCheckBox);
        cleanBuildFileCheckBox.addActionListener((event) -> setting.getState().cleanBuildFile = !setting.getState().cleanBuildFile);
        JBCheckBox openOutputFolderCheckBox = new JBCheckBox("Open output folder", setting.getState().openOutputFolder);
        titledPanel.add(openOutputFolderCheckBox);
        openOutputFolderCheckBox.addActionListener((event) -> setting.getState().openOutputFolder = !setting.getState().openOutputFolder);
        add2View(titledPanel);
    }
}
