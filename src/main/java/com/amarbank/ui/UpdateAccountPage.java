package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
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
 * Feature 7: Update Account. Load an existing account by number, then edit
 * the holder name and type-specific value (interest rate or overdraft limit).
 */
public class UpdateAccountPage extends JFrame {

    private final BankManagement bank;

    private final JTextField accountField = new JTextField(18);
    private final JTextField nameField = new JTextField(18);
    private final JTextField specialField = new JTextField(18);
    private final JLabel typeLabel = new JLabel("-");
    private final JLabel balanceLabel = new JLabel("-");
    private final JLabel specialLabel = new JLabel("Interest Rate (%):");

    private Account loadedAccount;

    public UpdateAccountPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Update Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 340);
        setLocationRelativeTo(null);

        add(buildSearch(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildSearch() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> loadAccount());
        panel.add(new JLabel("Account Number:"));
        panel.add(accountField);
        panel.add(loadButton);
        return panel;
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Account Type:"), c);
        c.gridx = 1;
        panel.add(typeLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Balance:"), c);
        c.gridx = 1;
        panel.add(balanceLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Holder Name:"), c);
        c.gridx = 1;
        panel.add(nameField, c);

        c.gridx = 0;
        c.gridy = 3;
        panel.add(specialLabel, c);
        c.gridx = 1;
        panel.add(specialField, c);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back");
        saveButton.addActionListener(e -> save());
        backButton.addActionListener(e -> dispose());
        panel.add(saveButton);
        panel.add(backButton);
        return panel;
    }

    private void loadAccount() {
        String number = accountField.getText().trim();
        if (!Validators.isValidAccountNumber(number)) {
            warn("Enter a valid account number (e.g. AB000001).");
            return;
        }
        try {
            loadedAccount = bank.findAccount(number);
            typeLabel.setText(loadedAccount.getAccountType());
            balanceLabel.setText(String.format("%.2f", loadedAccount.getBalance()));
            nameField.setText(loadedAccount.getAccountHolderName());
            specialField.setText(String.format("%.2f", loadedAccount.getSpecialAttribute()));
            if (loadedAccount instanceof SavingsAccount) {
                specialLabel.setText("Interest Rate (%):");
            } else {
                specialLabel.setText("Overdraft Limit:");
            }
        } catch (AccountNotFoundException ex) {
            loadedAccount = null;
            clearForm();
            error(ex.getMessage());
        }
    }

    private void save() {
        if (loadedAccount == null) {
            warn("Load an account first.");
            return;
        }

        String name = nameField.getText().trim();
        String special = specialField.getText().trim();

        if (!Validators.isValidName(name)) {
            warn("Enter a valid holder name (letters, spaces, dots).");
            return;
        }
        if (!Validators.isValidAmount(special)) {
            warn(loadedAccount instanceof SavingsAccount
                    ? "Enter a valid interest rate."
                    : "Enter a valid overdraft limit.");
            return;
        }

        try {
            Account updated = bank.updateAccountInfo(
                    loadedAccount.getAccountNumber(), name, Double.parseDouble(special));
            loadedAccount = updated;
            nameField.setText(updated.getAccountHolderName());
            specialField.setText(String.format("%.2f", updated.getSpecialAttribute()));
            info("Account updated.\n\n" + updated);
        } catch (AccountNotFoundException ex) {
            error(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        }
    }

    private void clearForm() {
        typeLabel.setText("-");
        balanceLabel.setText("-");
        nameField.setText("");
        specialField.setText("");
        specialLabel.setText("Interest Rate (%):");
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
