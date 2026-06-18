package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
import com.amarbank.model.AccountSchema;
import com.amarbank.model.FieldDescriptor;
import com.amarbank.service.BankManagement;
import com.amarbank.util.ValidationMessages;
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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Feature 5: Balance Check / Account Details. Look up one account by number,
 * and view every account in a {@link JTable} (Refresh reloads the list).
 */
public class AccountDetailsPage extends JFrame {

    private static final String[] FIXED_COLUMNS =
            {"Account Number", "Type", "Holder", "Balance", "Interest/Overdraft"};

    private final BankManagement bank;
    private final JTextField accountField = new JTextField(14);
    private final DefaultTableModel tableModel = new DefaultTableModel(buildColumns(), 0) {
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
        searchButton.addActionListener(this::onShowDetails);
        panel.add(new JLabel("Account Number:"));
        panel.add(accountField);
        panel.add(searchButton);
        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        refreshButton.addActionListener(this::onRefresh);
        backButton.addActionListener(this::onBack);
        panel.add(refreshButton);
        panel.add(backButton);
        return panel;
    }

    private void onShowDetails(ActionEvent event) {
        showDetails();
    }

    private void onRefresh(ActionEvent event) {
        refresh();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void showDetails() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        try {
            Account account = bank.findAccount(number);
            JOptionPane.showMessageDialog(this, account.toString(),
                    MessageDialogs.TITLE_ACCOUNT_DETAILS, JOptionPane.INFORMATION_MESSAGE);
        } catch (AccountNotFoundException ex) {
            MessageDialogs.notFound(this, ex.getMessage());
        }
    }

    private void refresh() {
        tableModel.setRowCount(0);
        for (Account a : bank.getAllAccounts()) {
            List<Object> row = new ArrayList<>(Arrays.asList(
                    a.getAccountNumber(),
                    a.getAccountType(),
                    a.getAccountHolderName(),
                    String.format("%.2f", a.getBalance()),
                    String.format("%.2f", a.getSpecialAttribute())));
            for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
                Object value = field.read(a);
                row.add(value == null ? "" : value.toString());
            }
            tableModel.addRow(row.toArray());
        }
    }

    private static String[] buildColumns() {
        List<String> columns = new ArrayList<>(Arrays.asList(FIXED_COLUMNS));
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            columns.add(field.header());
        }
        return columns.toArray(new String[0]);
    }
}
