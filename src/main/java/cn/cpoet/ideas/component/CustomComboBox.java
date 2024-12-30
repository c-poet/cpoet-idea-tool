package cn.cpoet.ideas.component;

import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

/**
 * @author CPoet
 */
public class CustomComboBox<T> extends ComboBox<T> {
    public CustomComboBox() {
    }

    public CustomComboBox(int width) {
        super(width);
    }

    public CustomComboBox(@NotNull ComboBoxModel<T> model) {
        super(model);
    }

    public CustomComboBox(T @NotNull [] items) {
        super(items);
    }

    public CustomComboBox(T @NotNull [] items, int width) {
        super(items, width);
    }

    public CustomComboBox(@NotNull ComboBoxModel<T> model, int width) {
        super(model, width);
    }

    @SuppressWarnings("unchecked")
    public void customText(Function<T, String> func) {
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(func.apply((T) value));
                return this;
            }
        });
    }
}
