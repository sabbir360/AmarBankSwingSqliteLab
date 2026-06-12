package com.amarbank.model;

/**
 * Savings account: carries an interest rate and cannot go below zero.
 */
public class SavingsAccount extends Account {

    private double interestRate;

    public SavingsAccount(String accountNumber, String accountHolderName, double balance, double interestRate) {
        super(accountNumber, accountHolderName, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public double getSpecialAttribute() {
        return interestRate;
    }

    @Override
    public double getWithdrawableAmount() {
        return Math.max(0, getBalance());
    }

    /**
     * Applies one month of interest using the stored annual rate (percent).
     * Formula: balance × (rate / 100) / 12. Returns the interest credited.
     */
    public double applyMonthlyInterest() {
        if (getBalance() <= 0 || interestRate <= 0) {
            return 0;
        }
        double interest = getBalance() * (interestRate / 100.0) / 12.0;
        setBalance(getBalance() + interest);
        return interest;
    }

    @Override
    public boolean canWithdraw(double amount) {
        return amount > 0 && amount <= getWithdrawableAmount();
    }
}
