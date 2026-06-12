package com.amarbank.model;

/**
 * Current account: allows withdrawals down to a negative overdraft limit.
 */
public class CurrentAccount extends Account {

    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String accountHolderName, double balance, double overdraftLimit) {
        super(accountNumber, accountHolderName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }

    @Override
    public double getSpecialAttribute() {
        return overdraftLimit;
    }

    @Override
    public double getWithdrawableAmount() {
        return getBalance() + overdraftLimit;
    }

    /** Amount currently drawn from the overdraft (zero when balance is not negative). */
    public double getOverdraftUsed() {
        return getBalance() < 0 ? -getBalance() : 0;
    }

    /** Overdraft headroom still available after any negative balance. */
    public double getRemainingOverdraft() {
        return overdraftLimit - getOverdraftUsed();
    }

    @Override
    public boolean canWithdraw(double amount) {
        return amount > 0 && amount <= getWithdrawableAmount();
    }
}
