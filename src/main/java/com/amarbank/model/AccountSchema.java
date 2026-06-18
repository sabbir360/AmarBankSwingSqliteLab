package com.amarbank.model;

import java.util.List;

/**
 * Single source of truth for additive account fields.
 *
 * <p>To add a field: add a {@code private} attribute + getter/setter on
 * {@link Account}, then add one {@link FieldDescriptor} entry below. To remove
 * a field: delete its entry here (and its attribute). The DB schema, both
 * forms, the details table, and {@link Account#toString()} update automatically.
 */
public final class AccountSchema {

    public static final List<FieldDescriptor> EXTENSION_FIELDS = List.of(
            new FieldDescriptor(
                    "email",
                    "email",
                    "Email:",
                    FieldDescriptor.SqlType.TEXT,
                    false,
                    Account::getEmail,
                    Account::setEmail,
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
                    "Enter a valid email address."),
            new FieldDescriptor(
                    "phoneNumber",
                    "phoneNumber",
                    "Phone Number:",
                    FieldDescriptor.SqlType.TEXT,
                    true,
                    Account::getPhoneNumber,
                    Account::setPhoneNumber,
                    "^\\+?\\d{11,12}$",
                    "Enter a valid phone number."
            )
    );

    private AccountSchema() {
    }
}
