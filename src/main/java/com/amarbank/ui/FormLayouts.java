package com.amarbank.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Shared GridBag form layout used by the Swing pages.
 */
public final class FormLayouts {

    public static final class Form {
        private final JPanel panel;
        private final GridBagConstraints c;

        private Form(JPanel panel, GridBagConstraints c) {
            this.panel = panel;
            this.c = c;
        }

        public JPanel panel() {
            return panel;
        }

        public void addRow(int row, String label, JComponent field) {
            addRow(row, new JLabel(label), field);
        }

        public void addRow(int row, JLabel label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            panel.add(label, c);
            c.gridx = 1;
            panel.add(field, c);
        }
    }

    private FormLayouts() {
    }

    public static Form create(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        return new Form(panel, c);
    }
}
