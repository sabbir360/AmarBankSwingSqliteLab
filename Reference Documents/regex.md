# Regex Cheat Sheet

```
import java.util.regex.Pattern;
import java.util.regex.Matcher;
```

## Quick validation

```
boolean ok = Pattern.matches("^\\d+$", "12345");   // whole string is digits

// Reuse a compiled pattern (faster when called often)
Pattern DIGITS = Pattern.compile("^\\d+$");
boolean ok2 = DIGITS.matcher("12345").matches();
```

## Find / extract / replace

```
Matcher m = Pattern.compile("\\d+").matcher("abc 12 de 34");
while (m.find()) {
    System.out.println(m.group());   // 12, then 34
}

String cleaned = "a1b2c3".replaceAll("\\d", "");  // "abc"
String[] parts = "a,b,,c".split(",", -1);          // keep trailing empties
```

## Common building blocks

| Token | Meaning |
|-------|---------|
| `.` | any character |
| `\\d` | digit, `\\D` non-digit |
| `\\w` | word char `[A-Za-z0-9_]` |
| `\\s` | whitespace |
| `^` `$` | start / end of input |
| `*` `+` `?` | 0+, 1+, 0 or 1 |
| `{2,5}` | between 2 and 5 times |
| `[A-Za-z]` | character set |
| `(?=...)` | lookahead (must follow, not consumed) |
| `|` | or |

## Patterns used in AmarBank (`Validators.java`)

```
String NAME_REGEX           = "^[A-Za-z][A-Za-z .]{1,49}$"; // letter first, then letters/space/dot
String AMOUNT_REGEX         = "^\\d+(\\.\\d{1,2})?$";        // 100 or 100.50
String USERNAME_REGEX       = "^[A-Za-z0-9_]{3,20}$";
String PASSWORD_REGEX       = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$"; // >=6, has letter + digit
String ACCOUNT_NUMBER_REGEX = "^AB\\d{6}$";                  // AB000001
```

## Extra patterns you may need

```
String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
String PHONE = "^\\+?\\d{10,15}$";
String DATE  = "^\\d{4}-\\d{2}-\\d{2}$";          // 2026-06-12
String INT   = "^-?\\d+$";
String LETTERS_ONLY = "^[A-Za-z]+$";
```

## Pattern: a reusable validator helper

```
public static boolean matches(String pattern, String value) {
    return value != null && Pattern.matches(pattern, value.trim());
}
```
