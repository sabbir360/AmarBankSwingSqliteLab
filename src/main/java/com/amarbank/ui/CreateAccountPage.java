package com.amarbank.ui;

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
 * Feature 1: Account Creation. Pick a type (Savings/Current), enter the holder
 * name + initial deposit + the type's special value, validate with regex, then
 * delegate to {@link BankManagement#createAccount}.
 */
public class CreateAccountPage extends JFrame {

    private final BankManagement bank;

    private final JRadioButton savingsRadio = new JRadioButton("Savings", true);
    private final JRadioButton currentRadio = new JRadioButton("Current");
    private final JTextField nameField = new JTextField(18);
    private final JTextField depositField = new JTextField(18);
    private final JTextField specialField = new JTextField(18);
    private final JLabel specialLabel = new JLabel("Interest Rate (%):");

    public CreateAccountPage(BankManagement bank) {
        this.bank = bank;

        setTitle("Amar Bank - Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 320);
        setLocationRelativeTo(null);

        add(buildTypePanel(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildTypePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Account Type"));
        ButtonGroup group = new ButtonGroup();
        group.add(savingsRadio);
        group.add(currentRadio);
        savingsRadio.addActionListener(this::onSavingsSelected);
        currentRadio.addActionListener(this::onCurrentSelected);
        panel.add(savingsRadio);
        panel.add(currentRadio);
        return panel;
    }

    private JPanel buildForm() {
        FormLayouts.Form form = FormLayouts.create(8, 16, 8, 16);
        form.addRow(0, "Holder Name:", nameField);
        form.addRow(1, "Initial Deposit:", depositField);
        form.addRow(2, specialLabel, specialField);
        return form.panel();
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton backButton = new JButton("Back");
        createButton.addActionListener(this::onCreate);
        backButton.addActionListener(this::onBack);
        panel.add(createButton);
        panel.add(backButton);
        return panel;
    }

    private void onSavingsSelected(ActionEvent event) {
        specialLabel.setText("Interest Rate (%):");
    }

    private void onCurrentSelected(ActionEvent event) {
        specialLabel.setText("Overdraft Limit:");
    }

    private void onCreate(ActionEvent event) {
        create();
    }

    private void onBack(ActionEvent event) {
        dispose();
    }

    private void create() {
        String name = nameField.getText().trim();
        String deposit = depositField.getText().trim();
        String special = specialField.getText().trim();

        if (!Validators.isValidName(name)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_HOLDER_NAME);
            return;
        }
        if (!Validators.isValidAmount(deposit)) {
            MessageDialogs.warn(this, ValidationMessages.INVALID_INITIAL_DEPOSIT);
            return;
        }
        if (!Validators.isValidAmount(special)) {
            MessageDialogs.warn(this, ValidationMessages.invalidSpecialAttribute(savingsRadio.isSelected()));
            return;
        }

        String type = savingsRadio.isSelected() ? "SAVINGS" : "CURRENT";
        Account account = bank.createAccount(type, name,
                Double.parseDouble(deposit), Double.parseDouble(special));

        MessageDialogs.info(this, "Account created.\n\n" + account);
        dispose();
    }
}
