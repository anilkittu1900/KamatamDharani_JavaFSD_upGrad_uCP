package banking;
import banking.exceptions.*;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;
public class Main {
    private static final Bank bank = new Bank();
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        boolean running = true;
        System.out.println("=== Welcome to Banking System Simulator ===");
        while (running) {
            try {
                showMainMenu();
                int choice = readInt("Choose an option: ");
                switch (choice) {
                    case 1 -> createAccountFlow();
                    case 2 -> operateOnAccountFlow();
                    case 3 -> {
                        System.out.println("Exit");
                        running = false;
                    }
                    default -> throw new InvalidMenuChoiceException("Invalid main menu choice: " + choice);
                }
            } catch (InvalidMenuChoiceException e) {
                System.err.println(e.getMessage());
            } catch (InputMismatchException ime) {
                System.err.println("Invalid input. Please enter correct data types.");
                scanner.nextLine(); // clear buffer
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }
    private static void showMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Create an account");
        System.out.println("2. Perform operations on existing accounts(choose by account number)");
        System.out.println("3. Exit the program ");
    }
    private static void createAccountFlow() {
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();
        try {
            Account acc = bank.createAccount(name);
            System.out.println("Account created successfully!");
            System.out.println("Account Number: " + acc.getAccountNumber());
            System.out.println("Holder: " + acc.getHolderName());
            System.out.println("Balance: " + acc.getBalance());
        } catch (InvalidNameException e) {
            System.err.println("Could not create account: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating account: " + e.getMessage());
        }
    }
    private static void operateOnAccountFlow() {
        System.out.print("Enter account number: ");
        String accNum = scanner.nextLine().trim();
        try {
            Account acc = bank.getAccount(accNum);
            boolean back = false;
            while (!back) {
                showAccountMenu(acc);
                int choice = readInt("Choose account operation: ");
                switch (choice) {
                    case 1 -> depositFlow(acc);
                    case 2 -> withdrawFlow(acc);
                    case 3 -> transferFlow(acc);
                    case 4 -> showBalanceFlow(acc);
                    case 5 -> back = true;
                    default -> throw new InvalidMenuChoiceException("Invalid account menu choice: " + choice);
                }
            }
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InvalidMenuChoiceException e) {
            System.err.println(e.getMessage());
        } catch (InputMismatchException ime) {
            System.err.println("Input mismatch. Please try again.");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
    private static void showAccountMenu(Account acc) {
        System.out.println("\nAccount: " + acc.getAccountNumber() + " (" + acc.getHolderName() + ")");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Transfer");
        System.out.println("4. Show balance");
        System.out.println("5. Return to main menu");
    }
    private static void depositFlow(Account acc) {
        try {
            BigDecimal amount = readBigDecimal("Enter amount to deposit: ");
            bank.deposit(acc.getAccountNumber(), amount);
            System.out.println("Deposit successful. New balance: " + acc.getBalance());
        } catch (InvalidAmountException | AccountNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InputMismatchException ime) {
            System.err.println("Invalid amount input.");
            scanner.nextLine();
        }
    }
    private static void withdrawFlow(Account acc) {
        try {
            BigDecimal amount = readBigDecimal("Enter amount to withdraw: ");
            try {
                bank.withdraw(acc.getAccountNumber(), amount);
                System.out.println("Withdrawal successful. New balance: " + acc.getBalance());
            } catch (InsufficientBalanceException e) {
                System.err.println(e.getMessage());
            }
        } catch (InvalidAmountException | AccountNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InputMismatchException ime) {
            System.err.println("Invalid amount input.");
            scanner.nextLine();
        }
    }
    private static void transferFlow(Account acc) {
        System.out.print("Enter destination account number: ");
        String destAcc = scanner.nextLine().trim();
        try {
            BigDecimal amount = readBigDecimal("Enter amount to transfer: ");
            bank.transfer(acc.getAccountNumber(), destAcc, amount);
            System.out.println("Transfer successful. New balance: " + acc.getBalance());
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientBalanceException e) {
            System.err.println(e.getMessage());
        } catch (InputMismatchException ime) {
            System.err.println("Invalid amount input.");
            scanner.nextLine();
        }
    }
    private static void showBalanceFlow(Account acc) {
        try {
            Account fresh = bank.getAccount(acc.getAccountNumber());
            System.out.println("Account Number: " + fresh.getAccountNumber());
            System.out.println("Holder: " + fresh.getHolderName());
            System.out.println("Balance: " + fresh.getBalance());
        } catch (AccountNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
    private static int readInt(String prompt) throws InputMismatchException {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException nfe) {
            throw new InputMismatchException("Expected integer but got: " + line);
        }
    }
    private static BigDecimal readBigDecimal(String prompt) throws InputMismatchException {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try {
            return new BigDecimal(line).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException nfe) {
            throw new InputMismatchException("Expected numeric amount but got: " + line);
        }
    }
}