package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
import com.amarbank.model.CurrentAccount;
import com.amarbank.model.SavingsAccount;
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Account Number:"), c);
        c.gridx = 1;
        panel.add(accountField, c);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton interestButton = new JButton("Apply Monthly Interest");
        JButton limitButton = new JButton("Show Withdrawal Limit");
        JButton allInterestButton = new JButton("Interest All Savings");
        JButton backButton = new JButton("Back");

        interestButton.addActionListener(e -> applyInterest());
        limitButton.addActionListener(e -> showWithdrawalLimit());
        allInterestButton.addActionListener(e -> applyInterestToAll());
        backButton.addActionListener(e -> dispose());

        panel.add(interestButton);
        panel.add(limitButton);
        panel.add(allInterestButton);
        panel.add(backButton);
        return panel;
    }

    private void applyInterest() {
        String number = accountField.getText().trim();
        if (!Validators.isValidAccountNumber(number)) {
            warn("Enter a valid account number (e.g. AB000001).");
            return;
        }
        try {
            Account account = bank.findAccount(number);
            if (!(account instanceof SavingsAccount)) {
                warn("Monthly interest applies only to savings accounts.");
                return;
            }
            double interest = bank.applyMonthlyInterest(number);
            if (interest <= 0) {
                info("No interest credited (zero balance or zero rate).");
            } else {
                info(String.format(
                        "Interest applied.%n%nCredited : %.2f%nNew balance : %.2f",
                        interest, account.getBalance()));
            }
        } catch (AccountNotFoundException ex) {
            error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        }
    }

    private void applyInterestToAll() {
        double total = bank.applyMonthlyInterestToAllSavings();
        info(String.format("Total interest credited to all savings accounts: %.2f", total));
    }

    private void showWithdrawalLimit() {
        String number = accountField.getText().trim();
        if (!Validators.isValidAccountNumber(number)) {
            warn("Enter a valid account number (e.g. AB000001).");
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
            info(message);
        } catch (AccountNotFoundException ex) {
            error(ex.getMessage());
        }
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
