package com.banking.ui;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.DatabaseService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BankingMainFrame extends JFrame {
    private JTextField accountNumberField;
    private JTextField accountHolderField;
    private JTextField amountField;
    private JTextArea transactionHistoryArea;
    private Account currentAccount;
    private DatabaseService dbService;

    public BankingMainFrame() {
        setTitle("Banking Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        dbService = new DatabaseService();
        createUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Account Creation Panel
        JPanel accountPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account Information"));
        
        accountPanel.add(new JLabel("Account Number:"));
        accountNumberField = new JTextField();
        accountPanel.add(accountNumberField);
        
        accountPanel.add(new JLabel("Account Holder:"));
        accountHolderField = new JTextField();
        accountPanel.add(accountHolderField);

        // Transaction Panel
        JPanel transactionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        
        transactionPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        transactionPanel.add(amountField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createAccountBtn = new JButton("Create Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton balanceBtn = new JButton("Check Balance");

        buttonPanel.add(createAccountBtn);
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(balanceBtn);

        // Transaction History Panel
        transactionHistoryArea = new JTextArea(10, 30);
        transactionHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(transactionHistoryArea);

        // Add action listeners
        createAccountBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        balanceBtn.addActionListener(e -> showBalance());

        // Add panels to main panel
        mainPanel.add(accountPanel, BorderLayout.NORTH);
        mainPanel.add(transactionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.EAST);

        add(mainPanel);
    }

    private void createAccount() {
        String accountNumber = accountNumberField.getText();
        String accountHolder = accountHolderField.getText();
        
        if (accountNumber.isEmpty() || accountHolder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account number and holder name");
            return;
        }

        Account newAccount = new Account(accountNumber, accountHolder);
        if (dbService.createAccount(newAccount)) {
            currentAccount = dbService.getAccount(accountNumber);
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            transactionHistoryArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create account!");
        }
    }

    private void deposit() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please create an account first");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive");
                return;
            }
            
            currentAccount.deposit(amount);
            if (dbService.updateAccountBalance(currentAccount.getId(), currentAccount.getBalance()) && 
                dbService.addTransaction(currentAccount.getId(), new Transaction("Deposit", amount))) {
                updateTransactionHistory();
                JOptionPane.showMessageDialog(this, "Deposit successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process deposit!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount");
        }
    }

    private void withdraw() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please create an account first");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive");
                return;
            }
            
            if (currentAccount.withdraw(amount)) {
                if (dbService.updateAccountBalance(currentAccount.getId(), currentAccount.getBalance()) && 
                    dbService.addTransaction(currentAccount.getId(), new Transaction("Withdrawal", amount))) {
                    updateTransactionHistory();
                    JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to process withdrawal!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient balance");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount");
        }
    }

    private void showBalance() {
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "Please create an account first");
            return;
        }
        JOptionPane.showMessageDialog(this, 
            String.format("Current Balance: %.2f", currentAccount.getBalance()));
    }

    private void updateTransactionHistory() {
        if (currentAccount == null) return;
        
        List<Transaction> history = dbService.getTransactionHistory(currentAccount.getId());
        StringBuilder sb = new StringBuilder();
        for (Transaction t : history) {
            sb.append(t.toString()).append("\n");
        }
        transactionHistoryArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingMainFrame frame = new BankingMainFrame();
            frame.setVisible(true);
        });
    }
}
