package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;


/**
 * AccountService handles the business logic related to account operations, including registration and login.
 * It interacts with the AccountRepository to perform actions on account data.
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository; // Repository for account-related data persistence
    /**
     * Constructor for injecting the AccountRepository dependency.
     * @param accountRepository Repository for account-related data.
     */
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    /**
     * Method to log in a user.
     * Validates the provided username and password, and attempts to authenticate the user.
     * @param account The account containing the username and password to be authenticated.
     * @return A ResponseEntity containing the authenticated account or an error message.
     */
    public ResponseEntity<Account> login(Account account) {
        String username = account.getUsername();
        String password = account.getPassword();

        // Validate that the username and password are not empty
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            // Return a 400 Bad Request response if validation fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        // Attempt to authenticate the account by matching username and password
        Account accountAuthenticated = accountRepository.findByUsernameAndPassword(username, password);
        if (accountAuthenticated == null) {
            // Return a 401 Unauthorized response if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // Return a 200 OK response with the authenticated account details
        return ResponseEntity.ok(accountAuthenticated);
    }
    /**
     * Method to register a new account.
     * Validates the provided username and password, checks if the username already exists, and then saves the account.
     * @param account The account to be registered.
     * @return A ResponseEntity indicating the success or failure of the registration.
     */
    public ResponseEntity<Account> registerAccount(Account account) {
        // Validate that the username is not blank and password is at least 8 characters long
        if (account.getUsername() == null || account.getUsername().isBlank() || 
            account.getPassword() == null || account.getPassword().length() < 8) {
            // Return a 400 Bad Request response if validation fails
            return ResponseEntity.status(400).body(null);
        }
        // Check if the username already exists in the database
        if (doesUsernameExist(account.getUsername())) {
            // Return a 409 Conflict response if the username already exists
            return ResponseEntity.status(409).body(null);
        }
        // Save the new account to the database
        Account savedAccount = accountRepository.save(account);
        // Return a 200 OK response with the saved account details
        return ResponseEntity.status(HttpStatus.OK).body(savedAccount);
    }
    /**
     * Helper method to check if a username already exists in the database.
     * @param username The username to check.
     * @return True if the username exists, otherwise false.
     */
    public boolean doesUsernameExist(String username) {
        return accountRepository.existsByUsername(username);
    }
}