# HashMap & HashSet Cheat Sheet

```
import java.util.*;
```

## HashMap (key -> value)

```
Map<String, Account> byNumber = new HashMap<>();
byNumber.put("AB000001", account);

Account a = byNumber.get("AB000001");      // null if absent
Account b = byNumber.getOrDefault("AB000009", null);
boolean has = byNumber.containsKey("AB000001");
byNumber.remove("AB000001");
int size = byNumber.size();
```

## Iterate

```
for (Map.Entry<String, Account> e : byNumber.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
for (String key : byNumber.keySet()) { /* ... */ }
for (Account val : byNumber.values()) { /* ... */ }

byNumber.forEach((key, val) -> System.out.println(key + " " + val));
```

## Handy methods

```
// Count occurrences
Map<String, Integer> count = new HashMap<>();
for (String word : words) count.merge(word, 1, Integer::sum);

// Group / accumulate
Map<String, List<Account>> byType = new HashMap<>();
byType.computeIfAbsent(account.getAccountType(), k -> new ArrayList<>()).add(account);
```

## Why use it over ArrayList?

`ArrayList` lookup by account number is a linear scan (`O(n)`). A
`HashMap<String, Account>` keyed by account number is `O(1)`. For AmarBank's
small list either is fine, but if an assignment asks for fast lookup, switch
`BankManagement` to keep both, or replace the list:

```
public class BankManagement {
    private final Map<String, Account> accounts = new HashMap<>();

    public Account findAccount(String number) throws AccountNotFoundException {
        Account a = accounts.get(number);
        if (a == null) throw new AccountNotFoundException("No account: " + number);
        return a;
    }
}
```

Note: the rubric specifically asks for `ArrayList<Account>`, so keep the list as
the primary store unless a question says otherwise.

## HashSet (unique values, no duplicates)

```
Set<String> seen = new HashSet<>();
boolean added = seen.add("AB000001");   // false if already present
boolean exists = seen.contains("AB000001");

// Deduplicate a list
List<String> unique = new ArrayList<>(new HashSet<>(rawList));
```

## LinkedHashMap / TreeMap

```
Map<String, Account> ordered = new LinkedHashMap<>();  // keeps insertion order
Map<String, Account> sorted  = new TreeMap<>();        // keys sorted
```
