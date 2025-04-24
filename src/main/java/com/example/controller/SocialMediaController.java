package com.example.controller;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

/**
 * This class is annotated with @RestController to handle HTTP requests and responses.
 * It includes endpoints for account registration, login, message management (CRUD operations),
 * and fetching messages by account.
 */

@RestController
public class SocialMediaController {

    private final AccountService accountService; // Service layer to handle account-related logic
    private final MessageService messageService; // Service layer to handle message-related logic

    /**
     * Constructor injection for AccountService and MessageService dependencies.
     */
    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }
    /**
     * Endpoint to get messages by a specific account ID.
     * This endpoint handles GET requests to "/accounts/{accountId}/messages" where {accountId} is a path variable.
     * @param accountId The ID of the account whose messages are to be retrieved.
     * @return A ResponseEntity containing the list of messages for the given account.
     */
    @GetMapping("accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        return messageService.getMessagesByAccountId(accountId);
    }
    /* @GetMapping("/accounts/{accountId}/messages")
     *   public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable int accountId) {
     *       // Retrieve messages for the given accountId
     *       List<Message> messages = messageService.getMessagesByAccountId(accountId);
     *       // Return the messages, or an empty list if no messages are found
     *       if (messages.isEmpty()) {
     *           return ResponseEntity.ok().body(messages);  // Empty list if no messages are found
     *       }
     *      return ResponseEntity.ok().body(messages);  // Return the list of messages
     *   }
     * 
     */

    /**
     * Endpoint to register a new account.
     * This endpoint handles POST requests to "/register" and expects the account details in the request body.
     * @param account Account object containing the registration data.
     * @return A ResponseEntity containing the created Account object.
     */
    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        return accountService.registerAccount(account);
    }
    /*
     * public void registerAccount(Account account) throws AccountAlreadyExistsException {
     * if (accountRepository.existsByUsername(account.getUsername())) {
     * throw new AccountAlreadyExistsException("Account with username " + account.getUsername() + " already exists.");
     * }
     * accountRepository.save(account);
     * }
     */

    /**
     * Endpoint to update the message text of an existing message.
     * This endpoint handles PATCH requests to "/messages/{messageId}" where {messageId} is a path variable.
     * The updated message text is provided in the request body.
     * @param messageId The ID of the message to be updated.
     * @param newMessageText A Message object containing the new text for the message.
     * @return A ResponseEntity indicating the result of the update operation.
     */
    @PatchMapping("messages/{messageId}")
    public ResponseEntity<Object> updateMessageText(@PathVariable Integer messageId, @RequestBody Message newMessageText) {
        return messageService.updateMessageText(messageId, newMessageText);
    }
    /**
     * Endpoint to log in an existing account.
     * This endpoint handles POST requests to "/login" and expects the account credentials in the request body.
     * If successful, it returns the logged-in account, otherwise, an error message.
     * @param account Account object containing login credentials.
     * @return A ResponseEntity containing either the account object or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Account account) {
        try {
            // Attempt login via accountService, and return appropriate response
            ResponseEntity<Account> response = accountService.login(account);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (IllegalStateException e) {
            // Unauthorized if login fails due to incorrect credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    /**
     * Endpoint to create a new message.
     * This endpoint handles POST requests to "/messages" and expects the message details in the request body.
     * @param message Message object containing the message data.
     * @return A ResponseEntity containing the created Message object.
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }
    /**
     * Endpoint to get a message by its ID.
     * This endpoint handles GET requests to "/messages/{messageId}" where {messageId} is a path variable.
     * @param messageId The ID of the message to be retrieved.
     * @return A ResponseEntity containing the Message object with the given ID.
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        return messageService.getMessageById(messageId);
    }
    /**
     * Endpoint to get all messages.
     * This endpoint handles GET requests to "/messages" and returns a list of all messages.
     * @return A ResponseEntity containing the list of all messages.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
    /**
     * Endpoint to delete a message by its ID.
     * This endpoint handles DELETE requests to "/messages/{messageId}" where {messageId} is a path variable.
     * @param messageId The ID of the message to be deleted.
     * @return A ResponseEntity indicating the result of the delete operation.
     */
    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<Object> deleteMessage(@PathVariable Integer messageId) {
        return messageService.deleteMessageById(messageId);
    }
    /* 
     * // Handle MessageNotFoundException
     * @ExceptionHandler(MessageNotFoundException.class)
     * public ResponseEntity<String> handleMessageNotFound(MessageNotFoundException ex) {
     *     return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
     * }
     */
}