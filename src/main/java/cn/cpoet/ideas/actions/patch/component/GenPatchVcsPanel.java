package cn.cpoet.ideas.actions.patch.component;

import cn.cpoet.ideas.model.TreeNodeInfo;
import com.intellij.openapi.project.Project;

/**
 * @author CPoet
 */
public class GenPatchVcsPanel extends AbstractGenPatchPanel {

    public GenPatchVcsPanel(Project project) {
        super(project);
    }

    @Override
    protected String getPatchDesc() {
        return null;
    }

    @Override
    protected String getFileName() {
        return null;
    }

    @Override
    protected TreeNodeInfo[] getTreeCheckedNodes() {
        return new TreeNodeInfo[0];
    }
}
