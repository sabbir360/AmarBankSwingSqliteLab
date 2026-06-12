package com.amarbank.ui;

import com.amarbank.db.BankDatabase;
import com.amarbank.util.ValidationMessages;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

/**
 * Login window. Validates input with {@link Validators}, checks the
 * credentials against {@link BankDatabase}, then opens the {@link MenuPage}.
 * Default login: admin / admin123.
 */
public class LoginPage extends JFrame {

    private final BankDatabase db = new BankDatabase();
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginPage() {
        db.createTables();

        setTitle("Amar Bank - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JLabel buildHeader() {
        JLabel header = new JLabel("Amar Bank", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 24f));
        header.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
        return header;
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(8, 24, 8, 24);
        form.addRow(0, "Username:", usernameField);
        form.addRow(1, "Password:", passwordField);
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton clearButton = new JButton("Clear");
        loginButton.addActionListener(this::onLogin);
        clearButton.addActionListener(this::onClear);
        getRootPane().setDefaultButton(loginButton);
        panel.add(loginButton);
        panel.add(clearButton);
        return panel;
    }

    private void onLogin(ActionEvent event) {
        attemptLogin();
    }

    private void onClear(ActionEvent event) {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!Validators.isValidUsername(username) || !Validators.isValidPassword(password)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_USERNAME_PASSWORD);
            return;
        }
        if (!db.checkLogin(username, password)) {
            MessageDialogs.loginFailed(this, ValidationMessages.WRONG_CREDENTIALS);
            return;
        }
        dispose();
        new MenuPage().setVisible(true);
    }
}
