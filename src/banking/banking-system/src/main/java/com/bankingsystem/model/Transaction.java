
package com.bankingsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document("transactions")
public class Transaction {
    @Id private String id;
    private String transactionId;
    private String type;
    private double amount;
    private Date timestamp;
    private String status;
    private String sourceAccount;
    private String destinationAccount;

    public Transaction() {}
    public Transaction(String tid,String type,double amt,String status,String src,String dst){
        this.transactionId=tid;this.type=type;this.amount=amt;this.status=status;this.sourceAccount=src;this.destinationAccount=dst;
        this.timestamp=new Date();
    }
}
