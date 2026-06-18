package com.amarbank.model;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Metadata for one additive account field. The DB layer, the Swing forms, the
 * details table, and {@link Account#toString()} all read this instead of
 * hard-coding the field, so a new field plugs into the whole app from one
 * {@link AccountSchema} entry.
 */
public final class FieldDescriptor {

    /** SQLite storage type for the field. */
    public enum SqlType {
        TEXT, REAL
    }

    private final String key;
    private final String column;
    private final String label;
    private final SqlType sqlType;
    private final boolean required;
    private final Function<Account, Object> getter;
    private final BiConsumer<Account, String> setter;
    private final Pattern validation;
    private final String errorMessage;

    public FieldDescriptor(String key, String column, String label, SqlType sqlType,
                           boolean required, Function<Account, Object> getter,
                           BiConsumer<Account, String> setter, String validationRegex,
                           String errorMessage) {
        this.key = key;
        this.column = column;
        this.label = label;
        this.sqlType = sqlType;
        this.required = required;
        this.getter = getter;
        this.setter = setter;
        this.validation = validationRegex == null ? null : Pattern.compile(validationRegex);
        this.errorMessage = errorMessage;
    }

    public String key() {
        return key;
    }

    public String column() {
        return column;
    }

    /** Form label (e.g. "Email:"). */
    public String label() {
        return label;
    }

    /** Table/header label without the trailing colon. */
    public String header() {
        return label.endsWith(":") ? label.substring(0, label.length() - 1) : label;
    }

    public SqlType sqlType() {
        return sqlType;
    }

    /** not using
    public boolean required() {
        return required;
    }*/

    /** Current value of this field on the given account. */
    public Object read(Account account) {
        return getter.apply(account);
    }

    /**
     * Validates a raw input string. Returns the error message when invalid, or
     * {@code null} when valid. Optional fields accept a blank value.
     */
    public String validate(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            return required ? errorMessage : null;
        }
        if (validation != null && !validation.matcher(value).matches()) {
            return errorMessage;
        }
        return null;
    }

    /** Parses and stores a raw input string on the account. */
    public void apply(Account account, String raw) {
        setter.accept(account, raw == null ? "" : raw.trim());
    }
}
