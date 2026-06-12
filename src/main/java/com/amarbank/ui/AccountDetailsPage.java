package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
import com.amarbank.service.BankManagement;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * Feature 5: Balance Check / Account Details. Look up one account by number,
 * and view every account in a {@link JTable} (Refresh reloads the list).
 */
public class AccountDetailsPage extends JFrame {

    private static final String[] COLUMNS =
            {"Account Number", "Type", "Holder", "Balance", "Interest/Overdraft"};

    private final BankManagement bank;
    private final JTextField accountField = new JTextField(14);
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AccountDetailsPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Account Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(640, 400);
        setLocationRelativeTo(null);

        add(buildSearch(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildSearch() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton searchButton = new JButton("Show Details");
        searchButton.addActionListener(e -> showDetails());
        panel.add(new JLabel("Account Number:"));
        panel.add(accountField);
        panel.add(searchButton);
        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.addActionListener(e -> refresh());
        backButton.addActionListener(e -> dispose());
        panel.add(refreshButton);
        panel.add(backButton);
        return panel;
    }

    private void showDetails() {
        String number = accountField.getText().trim();
        if (!Validators.isValidAccountNumber(number)) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid account number (e.g. AB000001).",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            JOptionPane.showMessageDialog(this, account.toString(),
                    "Account Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (AccountNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Not Found", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        tableModel.setRowCount(0);
        for (Account a : bank.getAllAccounts()) {
            tableModel.addRow(new Object[]{
                    a.getAccountNumber(),
                    a.getAccountType(),
                    a.getAccountHolderName(),
                    String.format("%.2f", a.getBalance()),
                    String.format("%.2f", a.getSpecialAttribute())
            });
        }
    }
}
