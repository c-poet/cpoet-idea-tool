package cn.cpoet.ideas.ic.component;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * 垂直滚动面板
 *
 * @author CPoet
 */
public class ScrollVPane extends JBScrollPane {

    private final SimpleVPanel viewPanel;

    public ScrollVPane() {
        viewPanel = new SimpleVPanel();
        setViewportView(viewPanel);
    }

    public void add2View(JComponent component) {
        viewPanel.add(component);
    }

    public void remove4View(int index) {
        viewPanel.remove(index);
    }
}
