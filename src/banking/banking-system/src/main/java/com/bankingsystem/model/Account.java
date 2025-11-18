
package com.bankingsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

@Document("accounts")
public class Account {
    @Id private String id;
    private String accountNumber;
    private String holderName;
    private double balance;
    private String status;
    private Date createdAt;
    private List<String> transactionIds = new ArrayList<>();

    public Account() {}
    public Account(String acc, String name) {
        this.accountNumber = acc;
        this.holderName = name;
        this.balance = 0;
        this.status = "ACTIVE";
        this.createdAt = new Date();
    }
    // getters/setters omitted
}
