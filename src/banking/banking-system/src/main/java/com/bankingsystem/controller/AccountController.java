
package com.bankingsystem.controller;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Map<String,String> body) {
        return ResponseEntity.status(201).body(service.createAccount(body.get("holderName")));
    }

    @GetMapping("/{acc}")
    public ResponseEntity<Account> get(@PathVariable String acc) {
        return ResponseEntity.ok(service.getByAccountNumber(acc));
    }

    @PutMapping("/{acc}")
    public ResponseEntity<Account> update(@PathVariable String acc, @RequestBody Map<String,String> body) {
        return ResponseEntity.ok(service.updateAccount(acc, body.get("holderName")));
    }

    @DeleteMapping("/{acc}")
    public ResponseEntity<Void> delete(@PathVariable String acc) {
        service.deleteAccount(acc);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{acc}/deposit")
    public ResponseEntity<Transaction> deposit(@PathVariable String acc, @RequestBody Map<String,Object> body) {
        return ResponseEntity.ok(service.deposit(acc, ((Number)body.get("amount")).doubleValue()));
    }

    @PutMapping("/{acc}/withdraw")
    public ResponseEntity<Transaction> withdraw(@PathVariable String acc, @RequestBody Map<String,Object> body) {
        return ResponseEntity.ok(service.withdraw(acc, ((Number)body.get("amount")).doubleValue()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody Map<String,Object> body) {
        return ResponseEntity.ok(service.transfer(
            (String) body.get("sourceAccount"),
            (String) body.get("destinationAccount"),
            ((Number) body.get("amount")).doubleValue()
        ));
    }

    @GetMapping("/{acc}/transactions")
    public ResponseEntity<List<Transaction>> txns(@PathVariable String acc) {
        return ResponseEntity.ok(service.getTransactionsForAccount(acc));
    }
}
