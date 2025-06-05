package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:banking.db";
    private Connection connection;

    public DatabaseService() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create accounts table
            String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "account_number TEXT NOT NULL UNIQUE," +
                         "account_holder TEXT NOT NULL," +
                         "balance REAL DEFAULT 0.0)";
            stmt.execute(sql);

            // Create transactions table
            sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "account_id INTEGER," +
                         "type TEXT NOT NULL," +
                         "amount REAL NOT NULL," +
                         "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                         "FOREIGN KEY (account_id) REFERENCES accounts (id))";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createAccount(Account account) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO accounts(account_number, account_holder, balance) VALUES(?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getAccountHolder());
            pstmt.setDouble(3, account.getBalance());
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccount(String accountNumber) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM accounts WHERE account_number = ?")) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Account account = new Account(rs.getString("account_number"), rs.getString("account_holder"));
                account.setId(rs.getInt("id"));
                account.setBalance(rs.getDouble("balance"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAccountBalance(int accountId, double newBalance) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE accounts SET balance = ? WHERE id = ?")) {
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addTransaction(int accountId, Transaction transaction) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO transactions(account_id, type, amount) VALUES(?, ?, ?)")) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, transaction.getType());
            pstmt.setDouble(3, transaction.getAmount());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transaction> getTransactionHistory(int accountId) {
        List<Transaction> history = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC")) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("type"),
                        rs.getDouble("amount")
                );
                transaction.setId(rs.getInt("id"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                history.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
