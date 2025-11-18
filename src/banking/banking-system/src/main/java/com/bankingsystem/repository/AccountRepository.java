
package com.bankingsystem.repository;

import com.bankingsystem.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account,String> {
    Optional<Account> findByAccountNumber(String acc);
    boolean existsByAccountNumber(String acc);
}
