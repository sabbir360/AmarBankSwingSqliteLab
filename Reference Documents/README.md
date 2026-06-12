# AmarBank Reference Documents

Copy-paste sample code to extend AmarBank or to answer a new assignment quickly
and accurately. Each file is a focused cheat sheet.

- [swing.md](swing.md) - containers, layouts, components, events, dialogs
- [regex.md](regex.md) - `Pattern`/`Matcher` and common validation patterns
- [arrays.md](arrays.md) - arrays vs `ArrayList`, iterate/sort/search, CSV split
- [hashmap.md](hashmap.md) - `HashMap`/`HashSet`, fast account lookup
- [sqlite.md](sqlite.md) - JDBC connect/create/insert/select/update/delete

## Project structure (package per concern)

```
src/main/java/com/amarbank/
  Main.java                 # entry point -> ui.LoginPage
  model/                    # Account, SavingsAccount, CurrentAccount
  service/                  # BankManagement (controller), BankOperations
  db/                       # BankDatabase (SQLite persistence)
  exception/                # InsufficientFundsException, AccountNotFoundException
  util/                     # Validators (regex)
  ui/                       # LoginPage, MenuPage, CreateAccountPage,
                            #   OperationsPage, AccountDetailsPage
```

## How the project maps to the assignment rubric

This is the "assignment pattern": if a question changes the wording, the part
to edit is usually one package/file.

| Rubric section | Where it lives |
|----------------|----------------|
| 1. OOP Design (classes) [40] | `model/` (`Account` abstract, `SavingsAccount`, `CurrentAccount`) + `service/` (`BankOperations`, `BankManagement`) |
| 2. Menu features [10] | `ui/MenuPage` + one `ui/XxxPage` per feature |
| 3. Persistence [10] | `db/BankDatabase` (SQLite). CSV export = loop `getAllAccounts()` + `Account.toCsvRow()` |
| 4. Exception handling [5] | `exception/` types thrown in `service/`, caught in `ui/` pages |
| 5. Clean code [bonus 5] | `util/Validators` (regex), small single-concern classes |

## Recipe: add a feature

1. Create `XxxPage extends JFrame` (copy an existing page).
2. Add a button in `MenuPage` that opens it.
3. If it persists data, add one method in `BankDatabase`.

## Recipe: remove a feature

1. Delete the `XxxPage`.
2. Remove its button in `MenuPage`.

## Recipe: switch persistence to CSV (old assignment)

`BankManagement` already keeps an `ArrayList<Account>`. To save/load CSV instead
of SQLite, replace the `BankDatabase` calls with file I/O using
`Account.toCsvRow()` and `Account.create(...)` (see [arrays.md](arrays.md) for
the split/join helpers). Format:
`AccountNumber,AccountType,HolderName,Balance,SpecialAttribute`.

## Recipe: remove login

Set `Main` to launch `MenuPage` directly, delete `LoginPage`, and drop the
`users` table + `checkLogin`/`seedAdmin` in `BankDatabase`.

## Run

```bash
./run.sh
```

Default login: `admin` / `admin123`.
