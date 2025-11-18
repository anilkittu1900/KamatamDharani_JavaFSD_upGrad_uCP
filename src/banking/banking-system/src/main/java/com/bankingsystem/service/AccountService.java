
package com.bankingsystem.service;

import com.bankingsystem.model.*;
import com.bankingsystem.repository.*;
import com.bankingsystem.exception.*;
import com.bankingsystem.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService {
    private final AccountRepository accRepo;
    private final TransactionRepository txRepo;
    private final AtomicInteger counter=new AtomicInteger(1);

    public AccountService(AccountRepository a,TransactionRepository t){this.accRepo=a;this.txRepo=t;}

    public Account createAccount(String name){
        if(name==null||name.isBlank()) throw new InvalidAmountException("Name empty");
        String num=AccountNumberGenerator.generate(name);
        while(accRepo.existsByAccountNumber(num)) num=AccountNumberGenerator.generate(name);
        return accRepo.save(new Account(num,name));
    }

    public Account getByAccountNumber(String num){
        return accRepo.findByAccountNumber(num).orElseThrow(()->new AccountNotFoundException("Not found"));
    }

    public Account updateAccount(String num,String name){
        Account a=getByAccountNumber(num);
        a.setHolderName(name);
        return accRepo.save(a);
    }

    public void deleteAccount(String num){
        accRepo.delete(getByAccountNumber(num));
    }

    private String txid(){return "TXN-"+counter.getAndIncrement();}

    public Transaction deposit(String num,double amt){
        if(amt<=0) throw new InvalidAmountException("Invalid amt");
        Account a=getByAccountNumber(num);
        a.setBalance(a.getBalance()+amt);
        accRepo.save(a);
        Transaction t=new Transaction(txid(),"DEPOSIT",amt,"SUCCESS",null,num);
        Transaction saved=txRepo.save(t);
        a.getTransactionIds().add(saved.getId());
        accRepo.save(a);
        return saved;
    }

    public Transaction withdraw(String num,double amt){
        if(amt<=0) throw new InvalidAmountException("Invalid amt");
        Account a=getByAccountNumber(num);
        if(a.getBalance()<amt) throw new InsufficientBalanceException("Low balance");
        a.setBalance(a.getBalance()-amt);
        accRepo.save(a);
        Transaction t=new Transaction(txid(),"WITHDRAW",amt,"SUCCESS",num,null);
        Transaction saved=txRepo.save(t);
        a.getTransactionIds().add(saved.getId());
        accRepo.save(a);
        return saved;
    }

    public Transaction transfer(String s,String d,double amt){
        if(amt<=0) throw new InvalidAmountException("Invalid amt");
        if(s.equals(d)) throw new InvalidAmountException("Same account");
        Account src=getByAccountNumber(s);
        Account dst=getByAccountNumber(d);
        if(src.getBalance()<amt) throw new InsufficientBalanceException("Low balance");
        src.setBalance(src.getBalance()-amt); dst.setBalance(dst.getBalance()+amt);
        accRepo.save(src); accRepo.save(dst);
        Transaction t=new Transaction(txid(),"TRANSFER",amt,"SUCCESS",s,d);
        Transaction saved=txRepo.save(t);
        src.getTransactionIds().add(saved.getId());
        dst.getTransactionIds().add(saved.getId());
        accRepo.save(src); accRepo.save(dst);
        return saved;
    }

    public List<Transaction> getTransactionsForAccount(String num){
        getByAccountNumber(num);
        return txRepo.findBySourceAccountOrDestinationAccountOrderByTimestampDesc(num,num);
    }
}
