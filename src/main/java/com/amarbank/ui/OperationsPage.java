package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.model.Account;
import com.amarbank.service.BankManagement;
import com.amarbank.util.ValidationMessages;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Features 2-4: Deposit, Withdraw, Transfer. Pick a transaction type with radio
 * buttons; the form shows only the fields needed for that operation. Before
 * executing, a confirmation dialog displays full account details (both accounts
 * for transfers).
 */
public class OperationsPage extends JFrame {

    private final BankManagement bank;

    private final JRadioButton depositRadio = new JRadioButton("Deposit", true);
    private final JRadioButton withdrawRadio = new JRadioButton("Withdraw");
    private final JRadioButton transferRadio = new JRadioButton("Transfer");

    private final JLabel accountLabel = new JLabel("Account Number:");
    private final JTextField accountField = new JTextField(18);
    private final JTextField amountField = new JTextField(18);
    private final JLabel destinationLabel = new JLabel("Transfer To (account):");
    private final JTextField destinationField = new JTextField(18);
    private JPanel formPanel;

    public OperationsPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Operations");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 320);
        setLocationRelativeTo(null);

        add(buildTypePanel(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        updateFormForTransactionType();
    }

    private JPanel buildTypePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Type"));
        ButtonGroup group = new ButtonGroup();
        group.add(depositRadio);
        group.add(withdrawRadio);
        group.add(transferRadio);
        depositRadio.addActionListener(this::onTransactionTypeChanged);
        withdrawRadio.addActionListener(this::onTransactionTypeChanged);
        transferRadio.addActionListener(this::onTransactionTypeChanged);
        panel.add(depositRadio);
        panel.add(withdrawRadio);
        panel.add(transferRadio);
        return panel;
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(8, 16, 8, 16);
        form.addRow(0, accountLabel, accountField);
        form.addRow(1, "Amount:", amountField);
        form.addRow(2, destinationLabel, destinationField);
        formPanel = form.panel();
        return formPanel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");
        submitButton.addActionListener(this::onSubmit);
        backButton.addActionListener(this::onBack);
        panel.add(submitButton);
        panel.add(backButton);
        return panel;
    }

    private void onTransactionTypeChanged(ActionEvent event) {
        updateFormForTransactionType();
    }

    private void updateFormForTransactionType() {
        boolean isTransfer = transferRadio.isSelected();
        destinationLabel.setVisible(isTransfer);
        destinationField.setVisible(isTransfer);
        accountLabel.setText(isTransfer ? "From Account:" : "Account Number:");
        formPanel.revalidate();
        formPanel.repaint();
    }

    private void onSubmit(ActionEvent event) {
        if (depositRadio.isSelected()) {
            deposit();
        } else if (withdrawRadio.isSelected()) {
            withdraw();
        } else {
            transfer();
        }
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void deposit() {
        if (invalidCommonInput()) {
            return;
        }
        try {
            Account account = bank.findAccount(accountField.getText().trim());
            if (confirmOperation("Deposit", account, null)) {
                bank.deposit(account.getAccountNumber(), amount());
                MessageDialogs.info(this, "Deposit successful.");
            }
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private void withdraw() {
        if (invalidCommonInput()) {
            return;
        }
        try {
            Account account = bank.findAccount(accountField.getText().trim());
            if (confirmOperation("Withdrawal", account, null)) {
                bank.withdraw(account.getAccountNumber(), amount());
                MessageDialogs.info(this, "Withdrawal successful.");
            }
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private void transfer() {
        if (invalidCommonInput()) {
            return;
        }
        String destination = destinationField.getText().trim();
        if (Validators.isInvalidAccountNumber(destination)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_DESTINATION_ACCOUNT_NUMBER);
            return;
        }
        try {
            Account source = bank.findAccount(accountField.getText().trim());
            Account target = bank.findAccount(destination);
            if (source.getAccountNumber().equalsIgnoreCase(target.getAccountNumber())) {
                MessageDialogs.warn(this, "Cannot transfer to the same account.");
                return;
            }
            if (confirmOperation("Transfer", source, target)) {
                bank.transfer(source.getAccountNumber(), target.getAccountNumber(), amount());
                MessageDialogs.info(this, "Transfer successful.");
            }
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private boolean confirmOperation(String operation, Account source, Account destination) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Confirm %s of %.2f%n%n", operation, amount()));
        message.append("--- Account ---\n").append(formatAccountSummary(source));
        if (destination != null) {
            message.append("\n\n--- Destination Account ---\n").append(formatAccountSummary(destination));
        }
        return MessageDialogs.confirm(this, message.toString());
    }

    private String formatAccountSummary(Account account) {
        return String.format(
                "Account Number : %s%nHolder         : %s%nType           : %s",
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getAccountType());
    }

    private boolean invalidCommonInput() {
        if (Validators.isInvalidAccountNumber(accountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return true;
        }
        if (Validators.isInvalidAmount(amountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_AMOUNT);
            return true;
        }
        return false;
    }

    private double amount() {
        return Double.parseDouble(amountField.getText().trim());
    }

}
