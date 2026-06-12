package com.amarbank.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Shared {@link JOptionPane} helpers and dialog titles used across Swing pages.
 */
public final class MessageDialogs {

    public static final String TITLE_INVALID_INPUT = "Invalid Input";
    public static final String TITLE_SUCCESS = "Success";
    public static final String TITLE_OPERATION_FAILED = "Operation Failed";
    public static final String TITLE_LOGIN_FAILED = "Login Failed";
    public static final String TITLE_NOT_FOUND = "Not Found";
    public static final String TITLE_ACCOUNT_DETAILS = "Account Details";

    private MessageDialogs() {

    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE_INVALID_INPUT, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE_OPERATION_FAILED, JOptionPane.ERROR_MESSAGE);
    }

    public static void loginFailed(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE_LOGIN_FAILED, JOptionPane.ERROR_MESSAGE);
    }

    public static void notFound(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE_NOT_FOUND, JOptionPane.ERROR_MESSAGE);
    }
}
