package com.amarbank.model;

/**
 * Abstract base class for every bank account.
 * Demonstrates encapsulation (private fields + getters/setters) and
 * abstraction (subclasses define their own type and withdrawal rule).
 */
public abstract class Account {

    private String accountNumber;
    private String accountHolderName;
    private double balance;

    protected Account(String accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Factory that builds the correct subclass from raw stored fields.
     * Used by persistence so account reconstruction lives in one place.
     */
    public static Account create(String type, String accountNumber, String holderName,
                                 double balance, double special) {
        if ("CURRENT".equalsIgnoreCase(type)) {
            return new CurrentAccount(accountNumber, holderName, balance, special);
        }
        return new SavingsAccount(accountNumber, holderName, balance, special);
    }

    /** "SAVINGS" or "CURRENT" — used by persistence and the UI. */
    public abstract String getAccountType();

    /** Interest rate (savings) or overdraft limit (current). */
    public abstract double getSpecialAttribute();

    /**
     * Maximum amount that can be withdrawn right now.
     * Savings: current balance (cannot go below zero).
     * Current: balance plus the overdraft limit.
     */
    public abstract double getWithdrawableAmount();

    /**
     * Polymorphic withdrawal rule. Savings allows up to the balance;
     * Current allows up to balance + overdraft limit.
     */
    public abstract boolean canWithdraw(double amount);

    /** Row for accounts.csv: AccountNumber,AccountType,HolderName,Balance,SpecialAttribute */
    public String toCsvRow() {
        return accountNumber + "," + getAccountType() + "," + accountHolderName
                + "," + balance + "," + getSpecialAttribute();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(
                "Account Number : %s%nType           : %s%nHolder         : %s%nBalance        : %.2f%n%-15s: %.2f",
                accountNumber, getAccountType(), accountHolderName, balance,
                getAccountType().equals("SAVINGS") ? "Interest Rate (%)" : "Overdraft Limit",
                getSpecialAttribute()));
        sb.append(String.format("%nWithdrawable    : %.2f", getWithdrawableAmount()));
        if (this instanceof CurrentAccount current) {
            sb.append(String.format("%nOverdraft Used  : %.2f%nOverdraft Left  : %.2f",
                    current.getOverdraftUsed(), current.getRemainingOverdraft()));
        }
        return sb.toString();
    }
}
