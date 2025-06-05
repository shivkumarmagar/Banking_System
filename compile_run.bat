@echo off

:: Compile Java files
javac -cp "lib/*" -d . src/main/java/com/banking/ui/BankingMainFrame.java src/main/java/com/banking/model/*.java src/main/java/com/banking/service/*.java

:: Run the application
java -cp "lib/*;." com.banking.ui.BankingMainFrame
