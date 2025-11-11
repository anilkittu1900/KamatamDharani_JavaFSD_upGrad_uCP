package banking;
import banking.exceptions.InsufficientBalanceException;
import banking.exceptions.InvalidAmountException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
public class Account {
    private final String accountNumber;
    private final String holderName;
    private BigDecimal balance;
    public Account(String accountNumber, String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("holderName cannot be empty");
        }
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.holderName = holderName.trim();
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public String getHolderName() {
        return holderName;
    }
    public synchronized BigDecimal getBalance() {
        return balance;
    }
    public synchronized void deposit(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        balance = balance.add(amount.setScale(2, RoundingMode.HALF_UP));
    }
    public synchronized void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance: " + balance);
        }
        balance = balance.subtract(amount.setScale(2, RoundingMode.HALF_UP));
    }
    public void transferTo(Account destination, java.math.BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        if (destination == null) {
            throw new IllegalArgumentException("Destination account cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive.");
        }
        Account first = this;
        Account second = destination;
        if (this.accountNumber.compareTo(destination.accountNumber) > 0) {
            first = destination;
            second = this;
        }
        synchronized (first) {
            synchronized (second) {
                if (this.balance.compareTo(amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance for transfer. Current balance: " + balance);
                }
                this.balance = this.balance.subtract(amount.setScale(2, RoundingMode.HALF_UP));
                destination.balance = destination.balance.add(amount.setScale(2, RoundingMode.HALF_UP));
            }
        }
    }
    @Override
    public String toString() {
        return String.format("Account[%s] - %s - Balance: %s", accountNumber, holderName, balance);
    }
}
