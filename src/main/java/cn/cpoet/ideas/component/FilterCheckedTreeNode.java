package cn.cpoet.ideas.component;

import cn.cpoet.ideas.model.TreeNodeInfo;
import com.intellij.ui.CheckedTreeNode;

/**
 * @author CPoet
 */
public class FilterCheckedTreeNode extends CheckedTreeNode {

    private static final long serialVersionUID = -3544383260950329368L;

    /** 原节点 */
    private CheckedTreeNode originNode;

    public FilterCheckedTreeNode() {
    }

    public FilterCheckedTreeNode(TreeNodeInfo userObject) {
        super(userObject);
    }

    public CheckedTreeNode getOriginNode() {
        return originNode;
    }

    public void setOriginNode(CheckedTreeNode originNode) {
        this.originNode = originNode;
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (originNode != null) {
            originNode.setChecked(checked);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (originNode != null) {
            originNode.setEnabled(enabled);
        }
    }
}
