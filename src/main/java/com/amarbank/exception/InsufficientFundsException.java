package com.amarbank.exception;

/**
 * Thrown when a withdrawal or transfer exceeds the available balance
 * (plus overdraft limit, for current accounts).
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
