package cn.cpoet.tool.actions.patch.component;

import cn.cpoet.tool.actions.patch.setting.GenPatchSetting;
import cn.cpoet.tool.i18n.I18n;
import cn.cpoet.tool.model.TreeNodeInfo;
import cn.cpoet.tool.util.NotificationUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.PotemkinProgress;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.util.ui.JBDimension;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 补丁包生成视图
 *
 * @author CPoet
 */
public class GenPatchPanel extends AbstractGenPatchPanel {

    private final static Logger LOGGER = LoggerFactory.getLogger(GenPatchPanel.class);

    /** 预览操作 */
    private Action previewAction;
    /** Dialog */
    private final DialogWrapper dialogWrapper;
    /** 选中统计 */
    private final AtomicInteger checkedCount;
    /** 文件树视图 */
    private final GenPatchTreePanel treePanel;
    /** 配置视图 */
    private final GenPatchConfPane confPanel;


    public GenPatchPanel(Project project, DialogWrapper dialogWrapper) {
        super(project);
        this.dialogWrapper = dialogWrapper;
        setPreferredSize(new JBDimension(setting.getState().width, setting.getState().height));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setting.getState().width = getWidth();
                setting.getState().height = getHeight();
            }
        });
        buildPreviewAction();
        treePanel = new GenPatchTreePanel(project);
        setFirstComponent(treePanel);
        confPanel = new GenPatchConfPane(project, this);
        setSecondComponent(confPanel);
        checkedCount = new AtomicInteger(getTreeCheckedNodes().length);
        addCheckboxTreeListener();
    }

    private void addCheckboxTreeListener() {
        treePanel.getTree().addCheckboxTreeListener(new CheckboxTreeListener() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                checkedCount.getAndAdd(node.isChecked() ? 1 : -1);
                updateBtnStatus();
            }
        });
        updateBtnStatus();
    }

    protected void buildPreviewAction() {
        previewAction = new TextAction(I18n.t("actions.patch.GenPatchPackageAction.preview")) {
            private static final long serialVersionUID = 1542378595944056560L;

            @Override
            public void actionPerformed(ActionEvent e) {
                preview();
            }
        };
        previewAction.setEnabled(false);
    }

    public Action getPreviewAction() {
        return previewAction;
    }

    public void generate() {
        ProgressIndicator indicator = startGenerateIndicator();
        GenPatchSetting.State state = setting.getState();
        indicator.setFraction(0.1);
        indicator.setText("Generate Patch info");
        getGenPatch()
                .then(patch -> {
                    indicator.setText("Generate patch");
                    indicator.setFraction(0.5);
                    return doGenerate(patch);
                })
                .onSuccess((path) -> {
                    indicator.setText("Generate after");
                    indicator.setFraction(0.8);
                    if (state.openOutputFolder) {
                        String patchPath = FilenameUtils.separatorsToSystem(path);
                        if (state.compress) {
                            cn.cpoet.tool.util.FileUtil.selectFile(patchPath);
                        } else {
                            cn.cpoet.tool.util.FileUtil.openFolder(patchPath);
                        }
                    }
                    state.lastFileNamePrefix = confPanel.getFileNamePrefix();
                    state.lastFileName = confPanel.getFileName();
                    indicator.setFraction(0.98);
                })
                .onError(e -> {
                    LOGGER.error("生成补丁失败: {}", e.getMessage(), e);
                    NotificationUtil.initBalloonError(e.getMessage()).notify(project);
                })
                .onProcessed(ret -> stopGenerateIndicator(indicator));
    }

    protected ProgressIndicator startGenerateIndicator() {
        PotemkinProgress progress = new PotemkinProgress("Generating", project, dialogWrapper.getContentPanel(), null);
        dialogWrapper.getWindow().setEnabled(false);
        progress.start();
        return progress;
    }

    protected void stopGenerateIndicator(ProgressIndicator progress) {
        progress.stop();
        dialogWrapper.getWindow().setEnabled(true);
    }

    public void preview() {
    }

    protected void updateBtnStatus() {
        GenPatchSetting.State state = setting.getState();
        if (checkedCount.get() > 0
                && StringUtils.isNotBlank(state.outputFolder)
                && StringUtils.isNotBlank(confPanel.getFileName())) {
            dialogWrapper.setOKActionEnabled(true);
        } else {
            dialogWrapper.setOKActionEnabled(false);
        }
    }

    @Override
    protected String getPatchDesc() {
        return treePanel.getPatchDesc();
    }

    @Override
    protected String getFileName() {
        return confPanel.getFileName();
    }

    @Override
    protected TreeNodeInfo[] getTreeCheckedNodes() {
        GenPatchTree tree = treePanel.getTree();
        return tree.getCheckedNodes(TreeNodeInfo.class, (nodeInfo) -> true);
    }
}
