# Arrays & ArrayList Cheat Sheet

```
import java.util.*;
```

## Arrays (fixed size)

```
int[] nums = {5, 3, 9, 1};
String[] names = new String[3];
names[0] = "Jane";

int len = nums.length;            // field, not method
for (int n : nums) System.out.println(n);
for (int i = 0; i < nums.length; i++) System.out.println(nums[i]);

Arrays.sort(nums);                // ascending in place
int idx = Arrays.binarySearch(nums, 9);   // only on a sorted array
String text = Arrays.toString(nums);       // "[1, 3, 5, 9]"
int[] copy = Arrays.copyOf(nums, nums.length);
Arrays.fill(names, "?");
```

## 2D arrays

```
int[][] grid = new int[2][3];
grid[0][1] = 7;
int[][] fixed = {{1, 2}, {3, 4}};
for (int[] row : fixed)
    for (int v : row) System.out.println(v);
```

## ArrayList (dynamic size) - what AmarBank uses

```
List<Account> accounts = new ArrayList<>();
accounts.add(account);
accounts.get(0);
accounts.size();
accounts.remove(account);          // or remove(index)
accounts.contains(account);
boolean empty = accounts.isEmpty();

for (Account a : accounts) { /* ... */ }

// Find by a field (linear scan) - see BankManagement.findAccount
for (Account a : accounts) {
    if (a.getAccountNumber().equalsIgnoreCase("AB000001")) return a;
}

// Remove while iterating (safe)
accounts.removeIf(a -> a.getBalance() == 0);
```

## Sorting objects

```
accounts.sort(Comparator.comparing(Account::getAccountHolderName));
accounts.sort(Comparator.comparingDouble(Account::getBalance).reversed());
```

## Array <-> List

```
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
String[] back = list.toArray(new String[0]);
```

## CSV split / join (for the CSV-export fallback)

```
// One CSV line -> fields
String line = "AB000001,SAVINGS,Jane Doe,1000.0,2.5";
String[] f = line.split(",", -1);   // -1 keeps trailing empty fields
String number = f[0];
double balance = Double.parseDouble(f[3]);

// Fields -> one CSV line
String row = String.join(",", "AB000001", "SAVINGS", "Jane Doe", "1000.0", "2.5");

// Whole file
import java.nio.file.*;
List<String> lines = Files.readAllLines(Paths.get("accounts.csv"));
Files.write(Paths.get("accounts.csv"), lines);
```
