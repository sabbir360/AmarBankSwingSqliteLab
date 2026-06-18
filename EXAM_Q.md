MITM 311: Advanced OOP

You are tasked with building the backend engine for “Amar Bank”, a terminal-based application. The system must allow users to create accounts, perform financial transactions, and maintain a permanent record of all accounts in a local CSV file.

⸻

1. Object-Oriented Design (Classes) [40 marks]

You must implement at least the following classes with proper encapsulation (private variables, public getters/setters):

• Account (Abstract or Base Class)

Holds core attributes like:

* accountNumber (String)
* accountHolderName (String)
* balance (double)

• SavingsAccount & CurrentAccount (Derived Classes)

Inherit from Account.

SavingsAccount

Should have an:

* interestRate attribute

CurrentAccount

Should have an:

* overdraftLimit attribute

• BankOperations (Class)

Defines the core methods:

* deposit()
* withdraw()
* transfer()

• BankManagement (Controller Class)

Contains the:

* ArrayList<Account> to manage active accounts
* Handles CSV reading/writing

⸻

2. Required Features (Terminal Menu) [10 marks]

The program must run in a loop, displaying a menu with the following options:

1. Account Creation

Ask the user for:

* Account type (Savings/Current)
* Name
* Initial Deposit

Automatically generate a unique Account Number.

Append the new account to the dynamic list.

2. Deposit

Input:

* Account number
* Amount

Update the balance.

3. Withdraw

Input:

* Account number
* Amount

Deduct from balance.

4. Transfer

Input:

* Source account number
* Destination account number
* Amount

Deduct from source and add to destination.

5. Balance Check / Account Details

Input account number to display:

* Current balance
* Holder details

6. Exit

Save all current data back to the CSV file and terminate the program.

⸻

3. File Handling (CSV Integration) [10 marks]

Startup

When the program starts, it must look for a file named:

accounts.csv

If it exists:

* Read the data
* Instantiate the correct account objects (Savings or Current)
* Populate the ArrayList

If it does not exist:

* Start with an empty list

Shutdown

When the user exits, the program must overwrite:

accounts.csv

with the updated data from the ArrayList in the following format:

AccountNumber,AccountType,HolderName,Balance,SpecialAttribute(Interest/Overdraft)

⸻

4. Exception Handling [5 marks]

Your program must not crash under invalid input or illegal operations.

You must create and throw a custom exception:

InsufficientFundsException

Thrown if a user attempts to:

* Withdraw more money than their balance
* Transfer more money than their balance (or overdraft limit allows)

⸻

5. Clean Programming [Bonus 5 marks]

Use:

* Proper OOP principles
* Meaningful class names
* Encapsulation
* Exception handling
* Readable code structure

Total: 70 marks (including bonus)
