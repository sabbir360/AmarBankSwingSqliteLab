package com.amarbank;

import com.amarbank.ui.MenuPage;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point. Launches the Swing login window on the EDT.
 */
public class Main {

    static void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
        SwingUtilities.invokeLater(() -> new MenuPage().setVisible(true));
    }
}
