package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.service.BankManagement;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Features 2-4: Deposit, Withdraw, Transfer. Shares an account-number and
 * amount field; Transfer also uses the destination field. All illegal
 * operations are caught and shown in a {@link JOptionPane}.
 */
public class OperationsPage extends JFrame {

    private final BankManagement bank;

    private final JTextField accountField = new JTextField(18);
    private final JTextField amountField = new JTextField(18);
    private final JTextField destinationField = new JTextField(18);

    public OperationsPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Operations");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 300);
        setLocationRelativeTo(null);

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Account Number:"), c);
        c.gridx = 1;
        panel.add(accountField, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Amount:"), c);
        c.gridx = 1;
        panel.add(amountField, c);

        c.gridx = 0; c.gridy = 2;
        panel.add(new JLabel("Transfer To (account):"), c);
        c.gridx = 1;
        panel.add(destinationField, c);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton backButton = new JButton("Back");
        depositButton.addActionListener(e -> deposit());
        withdrawButton.addActionListener(e -> withdraw());
        transferButton.addActionListener(e -> transfer());
        backButton.addActionListener(e -> dispose());
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(transferButton);
        panel.add(backButton);
        return panel;
    }

    private void deposit() {
        if (!validCommonInput()) {
            return;
        }
        try {
            bank.deposit(accountField.getText().trim(), amount());
            info("Deposit successful.");
        } catch (AccountNotFoundException ex) {
            error(ex.getMessage());
        }
    }

    private void withdraw() {
        if (!validCommonInput()) {
            return;
        }
        try {
            bank.withdraw(accountField.getText().trim(), amount());
            info("Withdrawal successful.");
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            error(ex.getMessage());
        }
    }

    private void transfer() {
        if (!validCommonInput()) {
            return;
        }
        String destination = destinationField.getText().trim();
        if (!Validators.isValidAccountNumber(destination)) {
            warn("Enter a valid destination account number (e.g. AB000001).");
            return;
        }
        try {
            bank.transfer(accountField.getText().trim(), destination, amount());
            info("Transfer successful.");
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        }
    }

    private boolean validCommonInput() {
        if (!Validators.isValidAccountNumber(accountField.getText().trim())) {
            warn("Enter a valid account number (e.g. AB000001).");
            return false;
        }
        if (!Validators.isValidAmount(amountField.getText().trim())) {
            warn("Enter a valid amount.");
            return false;
        }
        return true;
    }

    private double amount() {
        return Double.parseDouble(amountField.getText().trim());
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Operation Failed", JOptionPane.ERROR_MESSAGE);
    }
}
