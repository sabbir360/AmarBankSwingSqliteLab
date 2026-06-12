package com.amarbank.exception;

/**
 * Thrown when an account number cannot be found in the bank.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
