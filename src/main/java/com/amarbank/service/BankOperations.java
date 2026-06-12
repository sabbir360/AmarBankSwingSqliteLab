package com.amarbank.service;

import com.amarbank.exception.AccountNotFoundException;
import com.amarbank.exception.InsufficientFundsException;
import com.amarbank.model.Account;
import com.amarbank.model.SavingsAccount;

/**
 * Core financial operations: deposit, withdraw, transfer.
 * Methods are overloaded so callers can pass either an Account object or an
 * account number string.
 */
public class BankOperations {

    private final BankManagement bank;

    public BankOperations(BankManagement bank) {
        this.bank = bank;
    }

    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        account.setBalance(account.getBalance() + amount);
    }

    public void deposit(String accountNumber, double amount) throws AccountNotFoundException {
        deposit(bank.findAccount(accountNumber), amount);
    }

    public void withdraw(Account account, double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (!account.canWithdraw(amount)) {
            throw new InsufficientFundsException(
                    "Insufficient funds in account " + account.getAccountNumber()
                            + ". Available: " + String.format("%.2f", account.getWithdrawableAmount()) + ".");
        }
        account.setBalance(account.getBalance() - amount);
    }

    public void withdraw(String accountNumber, double amount)
            throws InsufficientFundsException, AccountNotFoundException {
        withdraw(bank.findAccount(accountNumber), amount);
    }

    public void transfer(String fromAccount, String toAccount, double amount)
            throws InsufficientFundsException, AccountNotFoundException {
        Account source = bank.findAccount(fromAccount);
        Account destination = bank.findAccount(toAccount);
        if (source == destination) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        withdraw(source, amount);
        deposit(destination, amount);
    }

    /** Credits one month of interest to a savings account. Returns interest earned. */
    public double applyMonthlyInterest(Account account) {
        if (!(account instanceof SavingsAccount savings)) {
            throw new IllegalArgumentException("Monthly interest applies only to savings accounts.");
        }
        return savings.applyMonthlyInterest();
    }
}
