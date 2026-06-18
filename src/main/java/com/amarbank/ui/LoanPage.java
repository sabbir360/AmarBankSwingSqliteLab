package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.model.Account;
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
 * Loan feature: take out a loan against an account, repay it, or view the
 * outstanding loan balance. Taking a loan credits the amount to the account
 * balance and records the debt; repaying draws from the available balance.
 */
public class LoanPage extends JFrame {

    private final BankManagement bank;
    private final JTextField accountField = new JTextField(18);
    private final JTextField amountField = new JTextField(18);

    public LoanPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Loans");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 240);
        setLocationRelativeTo(null);

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(12, 16, 12, 16);
        form.addRow(0, "Account Number:", accountField);
        form.addRow(1, "Amount:", amountField);
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton takeButton = new JButton("Take Loan");
        JButton repayButton = new JButton("Repay Loan");
        JButton statusButton = new JButton("Loan Status");
        JButton backButton = new JButton("Back");

        takeButton.addActionListener(this::onTakeLoan);
        repayButton.addActionListener(this::onRepayLoan);
        statusButton.addActionListener(this::onShowStatus);
        backButton.addActionListener(this::onBack);

        panel.add(takeButton);
        panel.add(repayButton);
        panel.add(statusButton);
        panel.add(backButton);
        return panel;
    }

    private void onTakeLoan(ActionEvent event) {
        takeLoan();
    }

    private void onRepayLoan(ActionEvent event) {
        repayLoan();
    }

    private void onShowStatus(ActionEvent event) {
        showStatus();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void takeLoan() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        if (Validators.isInvalidAmount(amountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_LOAN_AMOUNT);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            double amount = amount();
            if (cancelled("Take Loan", amount, account)) {
                return;
            }
            double loan = bank.takeLoan(number, amount);
            MessageDialogs.info(this, String.format(
                    "Loan disbursed.%n%nDisbursed     : %.2f%nNew balance   : %.2f%nLoan balance  : %.2f",
                    amount, account.getBalance(), loan));
            clearForm();
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageDialogs.warn(this, ex.getMessage());
        }
    }

    private void repayLoan() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        if (Validators.isInvalidAmount(amountField.getText().trim())) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_REPAYMENT_AMOUNT);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            double amount = amount();
            if (cancelled("Repay Loan", amount, account)) {
                return;
            }
            double loan = bank.repayLoan(number, amount);
            MessageDialogs.info(this, String.format(
                    "Repayment successful.%n%nRepaid        : %.2f%nNew balance   : %.2f%nLoan balance  : %.2f",
                    amount, account.getBalance(), loan));
            clearForm();
        } catch (InsufficientFundsException | AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageDialogs.warn(this, ex.getMessage());
        }
    }

    private void showStatus() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            MessageDialogs.info(this, String.format(
                    "Account       : %s (%s)%nHolder        : %s%nBalance       : %.2f%nLoan balance  : %.2f",
                    account.getAccountNumber(), account.getAccountType(),
                    account.getAccountHolderName(), account.getBalance(),
                    account.getLoanBalance()));
        } catch (AccountNotFoundException ex) {
            MessageDialogs.notFound(this, ex.getMessage());
        }
    }

    private boolean cancelled(String operation, double amount, Account account) {
        return !MessageDialogs.confirm(this, String.format(
                "Confirm %s of %.2f%n%nAccount Number : %s%nHolder         : %s%nType           : %s%n"
                        + "Balance        : %.2f%nLoan Balance   : %.2f",
                operation, amount, account.getAccountNumber(), account.getAccountHolderName(),
                account.getAccountType(), account.getBalance(), account.getLoanBalance()));
    }

    private double amount() {
        return Double.parseDouble(amountField.getText().trim());
    }

    private void clearForm() {
        accountField.setText("");
        amountField.setText("");
    }
}
