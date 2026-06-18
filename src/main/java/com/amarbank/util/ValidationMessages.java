package com.amarbank.util;

/**
 * Central home for every user-facing validation message. Change a message here
 * and the whole app follows. Used by the Swing pages for input validation.
 */
public final class ValidationMessages {

    public static final String ACCOUNT_NUMBER_EXAMPLE = "AC0001";

    public static final String INVALID_ACCOUNT_NUMBER =
            "Enter a valid account number (e.g. " + ACCOUNT_NUMBER_EXAMPLE + ").";
    public static final String INVALID_DESTINATION_ACCOUNT_NUMBER =
            "Enter a valid destination account number (e.g. " + ACCOUNT_NUMBER_EXAMPLE + ").";
    public static final String INVALID_AMOUNT = "Enter a valid amount.";
    public static final String INVALID_LOAN_AMOUNT = "Enter a valid loan amount.";
    public static final String INVALID_REPAYMENT_AMOUNT = "Enter a valid repayment amount.";
    public static final String INVALID_INITIAL_DEPOSIT = "Enter a valid initial deposit amount.";
    public static final String INVALID_HOLDER_NAME =
            "Enter a valid holder name (letters, spaces, dots).";
    public static final String INVALID_INTEREST_RATE = "Enter a valid interest rate.";
    public static final String INVALID_OVERDRAFT_LIMIT = "Enter a valid overdraft limit.";
    public static final String INVALID_USERNAME_PASSWORD = "Enter a valid username and password.";
    public static final String WRONG_CREDENTIALS = "Wrong username or password.";
    public static final String LOAD_ACCOUNT_FIRST = "Load an account first.";
    public static final String SAVINGS_INTEREST_ONLY =
            "Monthly interest applies only to savings accounts.";

    private ValidationMessages() {

    }

    public static String invalidSpecialAttribute(boolean savings) {
        return savings ? INVALID_INTEREST_RATE : INVALID_OVERDRAFT_LIMIT;
    }
}
