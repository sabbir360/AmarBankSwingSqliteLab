package com.amarbank.ui;

import com.amarbank.model.Account;
import com.amarbank.service.BankManagement;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Feature 1: Account Creation. Pick a type (Savings/Current), enter the holder
 * name + initial deposit + the type's special value, validate with regex, then
 * delegate to {@link BankManagement#createAccount}.
 */
public class CreateAccountPage extends JFrame {

    private final BankManagement bank;

    private final JRadioButton savingsRadio = new JRadioButton("Savings", true);
    private final JRadioButton currentRadio = new JRadioButton("Current");
    private final JTextField nameField = new JTextField(18);
    private final JTextField depositField = new JTextField(18);
    private final JTextField specialField = new JTextField(18);
    private final JLabel specialLabel = new JLabel("Interest Rate (%):");

    public CreateAccountPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 320);
        setLocationRelativeTo(null);

        add(buildTypePanel(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildTypePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Account Type"));
        ButtonGroup group = new ButtonGroup();
        group.add(savingsRadio);
        group.add(currentRadio);
        savingsRadio.addActionListener(e -> specialLabel.setText("Interest Rate (%):"));
        currentRadio.addActionListener(e -> specialLabel.setText("Overdraft Limit:"));
        panel.add(savingsRadio);
        panel.add(currentRadio);
        return panel;
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Holder Name:"), c);
        c.gridx = 1;
        panel.add(nameField, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Initial Deposit:"), c);
        c.gridx = 1;
        panel.add(depositField, c);

        c.gridx = 0; c.gridy = 2;
        panel.add(specialLabel, c);
        c.gridx = 1;
        panel.add(specialField, c);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton backButton = new JButton("Back");
        createButton.addActionListener(e -> create());
        backButton.addActionListener(e -> dispose());
        panel.add(createButton);
        panel.add(backButton);
        return panel;
    }

    private void create() {
        String name = nameField.getText().trim();
        String deposit = depositField.getText().trim();
        String special = specialField.getText().trim();

        if (!Validators.isValidName(name)) {
            warn("Enter a valid holder name (letters, spaces, dots).");
            return;
        }
        if (!Validators.isValidAmount(deposit)) {
            warn("Enter a valid initial deposit amount.");
            return;
        }
        if (!Validators.isValidAmount(special)) {
            warn(savingsRadio.isSelected()
                    ? "Enter a valid interest rate."
                    : "Enter a valid overdraft limit.");
            return;
        }

        String type = savingsRadio.isSelected() ? "SAVINGS" : "CURRENT";
        Account account = bank.createAccount(type, name,
                Double.parseDouble(deposit), Double.parseDouble(special));

        JOptionPane.showMessageDialog(this,
                "Account created.\n\n" + account,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }
}
