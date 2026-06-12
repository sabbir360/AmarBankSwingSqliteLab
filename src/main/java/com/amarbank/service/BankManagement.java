package com.amarbank.service;

import com.amarbank.db.BankDatabase;
import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.model.Account;
import com.amarbank.model.CurrentAccount;
import com.amarbank.model.SavingsAccount;

import java.util.List;

/**
 * Controller class. Holds the {@code ArrayList<Account>} of active accounts,
 * delegates money movement to {@link BankOperations}, and persists every
 * change through {@link BankDatabase}.
 */
public class BankManagement {

    private final BankDatabase db = new BankDatabase();
    private final List<Account> accounts;
    private final BankOperations operations = new BankOperations(this);

    public BankManagement() {
        db.createTables();
        accounts = db.getAllAccounts();
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    /** Creates an account with an auto-generated number and persists it. */
    public Account createAccount(String type, String holderName, double initialDeposit, double special) {
        String number = generateAccountNumber(type);
        Account account = Account.create(type, number, holderName, initialDeposit, special);
        db.insertAccount(account);
        accounts.add(account);
        return account;
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        for (Account a : accounts) {
            if (a.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return a;
            }
        }
        throw new AccountNotFoundException("No account found with number: " + accountNumber);
    }

    public void deposit(String accountNumber, double amount) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        operations.deposit(account, amount);
        db.updateAccount(account);
    }

    public void withdraw(String accountNumber, double amount)
            throws InsufficientFundsException, AccountNotFoundException {
        Account account = findAccount(accountNumber);
        operations.withdraw(account, amount);
        db.updateAccount(account);
    }

    public void transfer(String fromAccount, String toAccount, double amount)
            throws InsufficientFundsException, AccountNotFoundException {
        Account source = findAccount(fromAccount);
        Account destination = findAccount(toAccount);
        operations.transfer(fromAccount, toAccount, amount);
        db.updateAccount(source);
        db.updateAccount(destination);
    }

    /** Applies monthly interest to one savings account and persists the new balance. */
    public double applyMonthlyInterest(String accountNumber) throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        double interest = operations.applyMonthlyInterest(account);
        db.updateAccount(account);
        return interest;
    }

    /** Applies monthly interest to every savings account. Returns total interest credited. */
    public double applyMonthlyInterestToAllSavings() {
        double total = 0;
        for (Account account : accounts) {
            if (account instanceof SavingsAccount) {
                total += operations.applyMonthlyInterest(account);
                db.updateAccount(account);
            }
        }
        return total;
    }

    /** Updates holder name and type-specific attribute (interest rate or overdraft limit). */
    public Account updateAccountInfo(String accountNumber, String holderName, double special)
            throws AccountNotFoundException {
        Account account = findAccount(accountNumber);
        account.setAccountHolderName(holderName);
        if (account instanceof SavingsAccount savings) {
            savings.setInterestRate(special);
        } else if (account instanceof CurrentAccount current) {
            if (special < current.getOverdraftUsed()) {
                throw new IllegalArgumentException(
                        "Overdraft limit cannot be less than the amount already overdrawn ("
                                + String.format("%.2f", current.getOverdraftUsed()) + ").");
            }
            current.setOverdraftLimit(special);
        }
        db.updateAccount(account);
        return account;
    }

    private String generateAccountNumber(String type) {
        String prefix = "SAVINGS".equalsIgnoreCase(type) ? "AS" : "AC";
        int max = 0;
        for (Account a : accounts) {
            String num = a.getAccountNumber();
            if (num != null && num.length() == 6 && num.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(num.substring(2)));
                } catch (NumberFormatException ignored) {
                    // skip malformed numbers
                }
            }
        }

        return String.format(prefix + "%04d", max + 1);
    }
}
