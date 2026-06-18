package com.amarbank.ui;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.model.Account;
import com.amarbank.model.AccountSchema;
import com.amarbank.model.FieldDescriptor;
import com.amarbank.model.SavingsAccount;
import com.amarbank.service.BankManagement;
import com.amarbank.util.ValidationMessages;
import com.amarbank.util.Validators;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private final Map<String, JTextField> extensionFields = new LinkedHashMap<>();

    private Account loadedAccount;

    public UpdateAccountPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Update Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 340 + 36 * AccountSchema.EXTENSION_FIELDS.size());
        setLocationRelativeTo(null);

        add(buildSearch(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildSearch() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(this::onLoadAccount);
        panel.add(new JLabel("Account Number:"));
        panel.add(accountField);
        panel.add(loadButton);
        return panel;
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(8, 16, 8, 16);
        form.addRow(0, "Account Type:", typeLabel);
        form.addRow(1, "Balance:", balanceLabel);
        form.addRow(2, "Holder Name:", nameField);
        form.addRow(3, specialLabel, specialField);
        int row = 4;
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            JTextField input = new JTextField(18);
            extensionFields.put(field.key(), input);
            form.addRow(row++, field.label(), input);
        }
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back");
        saveButton.addActionListener(this::onSave);
        backButton.addActionListener(this::onBack);
        panel.add(saveButton);
        panel.add(backButton);
        return panel;
    }

    private void onLoadAccount(ActionEvent event) {
        loadAccount();
    }

    private void onSave(ActionEvent event) {
        save();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void loadAccount() {
        String number = accountField.getText().trim();
        if (Validators.isInvalidAccountNumber(number)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_ACCOUNT_NUMBER);
            return;
        }
        try {
            loadedAccount = bank.findAccount(number);
            typeLabel.setText(loadedAccount.getAccountType());
            balanceLabel.setText(String.format("%.2f", loadedAccount.getBalance()));
            nameField.setText(loadedAccount.getAccountHolderName());
            specialField.setText(String.format("%.2f", loadedAccount.getSpecialAttribute()));
            populateExtensionFields(loadedAccount);
            if (loadedAccount instanceof SavingsAccount) {
                specialLabel.setText("Interest Rate (%):");
            } else {
                specialLabel.setText("Overdraft Limit:");
            }
        } catch (AccountNotFoundException ex) {
            loadedAccount = null;
            clearForm();
            MessageDialogs.error(this, ex.getMessage());
        }
    }

    private void save() {
        if (loadedAccount == null) {
            MessageDialogs.warn(this, ValidationMessages.LOAD_ACCOUNT_FIRST);
            return;
        }

        String name = nameField.getText().trim();
        String special = specialField.getText().trim();

        if (Validators.isInvalidName(name)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_HOLDER_NAME);
            return;
        }
        if (Validators.isInvalidAmount(special)) {
            MessageDialogs.warn(this, ValidationMessages.invalidSpecialAttribute(
                    loadedAccount instanceof SavingsAccount));
            return;
        }

        Map<String, String> extras = new LinkedHashMap<>();
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            String raw = extensionFields.get(field.key()).getText().trim();
            String error = field.validate(raw);
            if (error != null) {
                MessageDialogs.warn(this, error);
                return;
            }
            extras.put(field.key(), raw);
        }

        try {
            Account updated = bank.updateAccountInfo(
                    loadedAccount.getAccountNumber(), name, Double.parseDouble(special), extras);
            loadedAccount = updated;
            nameField.setText(updated.getAccountHolderName());
            specialField.setText(String.format("%.2f", updated.getSpecialAttribute()));
            populateExtensionFields(updated);
            MessageDialogs.info(this, "Account updated.\n\n" + updated);
        } catch (AccountNotFoundException ex) {
            MessageDialogs.error(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageDialogs.warn(this, ex.getMessage());
        }
    }

    private void clearForm() {
        typeLabel.setText("-");
        balanceLabel.setText("-");
        nameField.setText("");
        specialField.setText("");
        specialLabel.setText("Interest Rate (%):");
        for (JTextField input : extensionFields.values()) {
            input.setText("");
        }
    }

    private void populateExtensionFields(Account account) {
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            Object value = field.read(account);
            extensionFields.get(field.key()).setText(value == null ? "" : value.toString());
        }
    }
}
