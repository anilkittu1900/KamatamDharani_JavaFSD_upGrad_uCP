package banking;
import banking.exceptions.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
public class Bank {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    public Account createAccount(String fullName) throws InvalidNameException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidNameException("Name cannot be empty");
        }
        String initials = deriveInitials(fullName);
        String accountNumber;
        do {
            int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
            accountNumber = initials + rand;
        } while (accounts.containsKey(accountNumber));

        Account acc = new Account(accountNumber, fullName.trim());
        accounts.put(accountNumber, acc);
        return acc;
    }
    private String deriveInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
            if (sb.length() >= 3) break;
        }
        return sb.toString();
    }
    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account acc = accounts.get(accountNumber);
        if (acc == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return acc;
    }
    public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException, InvalidAmountException {
        Account acc = getAccount(accountNumber);
        try {
            acc.deposit(amount);
        } catch (InvalidAmountException e) {
            throw e;
        }
    }
    public void withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        Account acc = getAccount(accountNumber);
        try {
            acc.withdraw(amount);
        } catch (InvalidAmountException | InsufficientBalanceException e) {
            throw e;
        }
    }
    public void transfer(String fromAccNum, String toAccNum, BigDecimal amount) throws AccountNotFoundException, InvalidAmountException, InsufficientBalanceException {
        Account from = getAccount(fromAccNum);
        Account to = getAccount(toAccNum);
        from.transferTo(to, amount);
    }
    public List<Account> findAccountsByHolder(String holderNameFragment) {
        String q = holderNameFragment == null ? "" : holderNameFragment.trim().toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getHolderName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }
}