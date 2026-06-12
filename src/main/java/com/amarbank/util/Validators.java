package com.amarbank.util;

import java.util.regex.Pattern;

/**
 * Central home for every regex rule. Change a pattern here and the whole app
 * follows. Used by the Swing pages for input validation.
 */
public final class Validators {

    public static final String NAME_REGEX = "^[A-Za-z][A-Za-z .]{1,49}$";
    public static final String AMOUNT_REGEX = "^\\d+(\\.\\d{1,2})?$";
    public static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    public static final String ACCOUNT_NUMBER_REGEX = "^AB\\d{6}$";

    private static final Pattern NAME = Pattern.compile(NAME_REGEX);
    private static final Pattern AMOUNT = Pattern.compile(AMOUNT_REGEX);
    private static final Pattern USERNAME = Pattern.compile(USERNAME_REGEX);
    private static final Pattern PASSWORD = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern ACCOUNT_NUMBER = Pattern.compile(ACCOUNT_NUMBER_REGEX);

    private Validators() {

    }

    public static boolean isValidName(String value) {
        return value != null && NAME.matcher(value.trim()).matches();
    }

    public static boolean isValidAmount(String value) {
        return value != null && AMOUNT.matcher(value.trim()).matches();
    }

    public static boolean isValidUsername(String value) {
        return value != null && USERNAME.matcher(value.trim()).matches();
    }

    public static boolean isValidPassword(String value) {
        return value != null && PASSWORD.matcher(value).matches();
    }

    public static boolean isValidAccountNumber(String value) {
        return value != null && ACCOUNT_NUMBER.matcher(value.trim()).matches();
    }
}
