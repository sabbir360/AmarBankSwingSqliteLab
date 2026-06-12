package com.amarbank.ui;

import com.amarbank.db.BankDatabase;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Username:"), c);
        c.gridx = 1;
        panel.add(usernameField, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Password:"), c);
        c.gridx = 1;
        panel.add(passwordField, c);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton clearButton = new JButton("Clear");
        loginButton.addActionListener(e -> attemptLogin());
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });
        getRootPane().setDefaultButton(loginButton);
        panel.add(loginButton);
        panel.add(clearButton);
        return panel;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!Validators.isValidUsername(username) || !Validators.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid username and password.", "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!db.checkLogin(username, password)) {
            JOptionPane.showMessageDialog(this,
                    "Wrong username or password.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        new MenuPage().setVisible(true);
    }
}
