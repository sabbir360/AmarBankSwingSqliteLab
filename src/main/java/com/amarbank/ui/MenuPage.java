package com.amarbank.ui;

import com.amarbank.service.BankManagement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Landing menu (the Swing version of the terminal menu loop). One button per
 * feature. To add a feature: create a new {@code XxxPage} and add a button
 * here that opens it.
 */
public class MenuPage extends JFrame {

    private final BankManagement bank = new BankManagement();

    public MenuPage() {
        setTitle("Amar Bank - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);

        JLabel header = new JLabel("Main Menu", JLabel.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 20f));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        add(header, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(6, 1, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 24, 24, 24));

        JButton createButton = new JButton("Create Account");
        JButton updateButton = new JButton("Update Account");
        JButton operationsButton = new JButton("Deposit / Withdraw / Transfer");
        JButton detailsButton = new JButton("Account Details");
        JButton specialButton = new JButton("Interest & Overdraft");
        JButton logoutButton = new JButton("Logout");

        createButton.addActionListener(e -> new CreateAccountPage(bank).setVisible(true));
        updateButton.addActionListener(e -> new UpdateAccountPage(bank).setVisible(true));
        operationsButton.addActionListener(e -> new OperationsPage(bank).setVisible(true));
        detailsButton.addActionListener(e -> new AccountDetailsPage(bank).setVisible(true));
        specialButton.addActionListener(e -> new SpecialFeaturesPage(bank).setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage().setVisible(true);
        });

        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(operationsButton);
        buttons.add(detailsButton);
        buttons.add(specialButton);
        buttons.add(logoutButton);
        add(buttons, BorderLayout.CENTER);
    }
}
