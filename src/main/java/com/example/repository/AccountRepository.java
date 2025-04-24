package com.example.repository;

import com.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Method to check if an account exists by username
    boolean existsByUsername(String username);
    // Method to find an account by Username
    Account findByUsername(String username);
    // Method to find an account by username and password
    Account findByUsernameAndPassword(String username, String password); 
    // Method to find an account by AccountId
    Account findByAccountId(Integer accountId);
}