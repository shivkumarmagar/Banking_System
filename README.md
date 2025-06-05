# Banking Management System

A Java Swing-based banking management system that simulates basic banking operations with persistent storage using SQLite database.

## Features

- Account creation and management
- Deposit and withdrawal transactions
- Balance inquiry
- Transaction history
- User-friendly GUI with Java Swing
- Persistent data storage using SQLite

## Requirements

- Java JDK 17 or higher
- SQLite JDBC driver

## Setup

1. Clone the repository:
```bash
git clone https://github.com/shivkumarmagar/Banking_System.git
```

2. Compile and run the application:
```bash
javac -cp "lib/*" -d . src/main/java/com/banking/ui/BankingMainFrame.java src/main/java/com/banking/model/*.java src/main/java/com/banking/service/*.java
java -cp "lib/*;." com.banking.ui.BankingMainFrame
```

## Usage

1. Create a new account by entering an account number and holder name
2. Perform deposits and withdrawals
3. Check account balance
4. View transaction history

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/banking/
│           ├── model/        # Model classes (Account, Transaction)
│           ├── service/      # Database service
│           └── ui/          # User interface
└── lib/                     # External libraries
```

## License

MIT License
