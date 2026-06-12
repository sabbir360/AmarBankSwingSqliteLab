package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
import com.amarbank.model.CurrentAccount;
import com.amarbank.model.SavingsAccount;
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
 * Feature 6: Savings interest and current-account overdraft utilities.
 * Apply monthly interest to a savings account, or inspect withdrawal limits
 * (including overdraft headroom for current accounts).
 */
public class SpecialFeaturesPage extends JFrame {

    private final BankManagement bank;
    private final JTextField accountField = new JTextField(18);

    public SpecialFeaturesPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Interest & Overdraft");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 220);
        setLocationRelativeTo(null);

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(12, 16, 12, 16);
        form.addRow(0, "Account Number:", accountField);
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton interestButton = new JButton("Apply Monthly Interest");
        JButton limitButton = new JButton("Show Withdrawal Limit");
        JButton allInterestButton = new JButton("Interest All Savings");
        JButton backButton = new JButton("Back");

        interestButton.addActionListener(this::onApplyInterest);
        limitButton.addActionListener(this::onShowWithdrawalLimit);
        allInterestButton.addActionListener(this::onApplyInterestToAll);
        backButton.addActionListener(this::onBack);

        panel.add(interestButton);
        panel.add(limitButton);
        panel.add(allInterestButton);
        panel.add(backButton);
        return panel;
    }

    private void onApplyInterest(ActionEvent event) {
        applyInterest();
    }

    private void onShowWithdrawalLimit(ActionEvent event) {
        showWithdrawalLimit();
    }

    private void onApplyInterestToAll(ActionEvent event) {
        applyInterestToAll();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void applyInterest() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            if (!(account instanceof SavingsAccount)) {
                MessageDialogs.warn(this, ValidationMessages.SAVINGS_INTEREST_ONLY);
                return;
            }
            double interest = bank.applyMonthlyInterest(number);
            if (interest <= 0) {
                MessageDialogs.info(this, "No interest credited (zero balance or zero rate).");
            } else {
                MessageDialogs.info(this, String.format(
                        "Interest applied.%n%nCredited : %.2f%nNew balance : %.2f",
                        interest, account.getBalance()));
            }
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageDialogs.warn(this, ex.getMessage());
        }
    }

    private void applyInterestToAll() {
        double total = bank.applyMonthlyInterestToAllSavings();
        MessageDialogs.info(this, String.format("Total interest credited to all savings accounts: %.2f", total));
    }

    private void showWithdrawalLimit() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            String message;
            if (account instanceof CurrentAccount current) {
                message = String.format(
                        "Account     : %s (%s)%nBalance     : %.2f%nOverdraft   : %.2f%n"
                                + "Withdrawable: %.2f%nUsed        : %.2f%nRemaining   : %.2f",
                        account.getAccountNumber(), account.getAccountType(),
                        account.getBalance(), current.getOverdraftLimit(),
                        account.getWithdrawableAmount(), current.getOverdraftUsed(),
                        current.getRemainingOverdraft());
            } else {
                message = String.format(
                        "Account     : %s (%s)%nBalance     : %.2f%nInterest (%%): %.2f%n"
                                + "Withdrawable: %.2f",
                        account.getAccountNumber(), account.getAccountType(),
                        account.getBalance(), account.getSpecialAttribute(),
                        account.getWithdrawableAmount());
            }
            MessageDialogs.info(this, message);
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        }
    }
}
