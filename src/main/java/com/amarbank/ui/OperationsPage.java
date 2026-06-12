package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.service.BankManagement;
import com.amarbank.util.ValidationMessages;
import com.amarbank.util.Validators;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

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
        FormLayouts.Form form = FormLayouts.create(12, 16, 12, 16);
        form.addRow(0, "Account Number:", accountField);
        form.addRow(1, "Amount:", amountField);
        form.addRow(2, "Transfer To (account):", destinationField);
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton backButton = new JButton("Back");
        depositButton.addActionListener(this::onDeposit);
        withdrawButton.addActionListener(this::onWithdraw);
        transferButton.addActionListener(this::onTransfer);
        backButton.addActionListener(this::onBack);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(transferButton);
        panel.add(backButton);
        return panel;
    }

    private void onDeposit(ActionEvent event) {
        deposit();
    }

    private void onWithdraw(ActionEvent event) {
        withdraw();
    }

    private void onTransfer(ActionEvent event) {
        transfer();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void deposit() {
        if (!validCommonInput()) {
            return;
        }
        try {
            bank.deposit(accountField.getText().trim(), amount());
            MessageDialogs.info(this, "Deposit successful.");
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private void withdraw() {
        if (!validCommonInput()) {
            return;
        }
        try {
            bank.withdraw(accountField.getText().trim(), amount());
            MessageDialogs.info(this, "Withdrawal successful.");
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private void transfer() {
        if (!validCommonInput()) {
            return;
        }
        String destination = destinationField.getText().trim();
        if (!Validators.isValidAccountNumber(destination)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_DESTINATION_ACCOUNT_NUMBER);
            return;
        }
        try {
            bank.transfer(accountField.getText().trim(), destination, amount());
            MessageDialogs.info(this, "Transfer successful.");
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageDialogs.warn(this, ex.getMessage());
        }
    }

    private boolean validCommonInput() {
        if (!Validators.isValidAccountNumber(accountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return false;
        }
        if (!Validators.isValidAmount(amountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_AMOUNT);
            return false;
        }
        return true;
    }

    private double amount() {
        return Double.parseDouble(amountField.getText().trim());
    }

}
