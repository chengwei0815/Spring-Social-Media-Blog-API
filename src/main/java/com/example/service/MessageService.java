package com.example.service;


import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * MessageService handles business logic related to messages.
 * It interacts with the MessageRepository and AccountRepository to perform CRUD operations on messages.
 * The service includes validation checks and response handling for message-related actions.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository; // Repository for message data persistence
    private final AccountRepository accountRepository; // Repository for account data persistence

    /**
     * Constructor for injecting dependencies.
     * @param messageRepository Repository for message-related data.
     * @param accountRepository Repository for account-related data.
     */
    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }
    /**
     * Method to retrieve all messages from the database.
     * @return A list of all messages.
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    /**
     * Method to retrieve a message by its ID.
     * @param messageId The ID of the message to retrieve.
     * @return A ResponseEntity containing the message or an empty response if not found.
     */
    public ResponseEntity<Message> getMessageById(Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            return ResponseEntity.ok(optionalMessage.get());
        } else {
            return ResponseEntity.ok().build(); // No content if message not found
        }
        /*
         * public List<Message> getMessagesById(int messageId) {
         * return messageRepository.findByPostedBy(messageId);
         * }
         */
    }
    /**
     * Method to create a new message.
     * Validates the message content and checks if the account exists before saving the message.
     * @param message The message to be created.
     * @return A ResponseEntity with the created message or a bad request if validation fails.
     */
    public ResponseEntity<Message> createMessage(Message message) {
        // Validate message text and postedBy (accountId)
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
                message.getMessageText().length() > 255 || message.getPostedBy() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        // Check if the account associated with the message exists
        String username = getUsernameByAccountId(message.getPostedBy());
        if (username == null || !accountRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Save the new message and return the saved message
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }
    /**
     * Method to retrieve messages by account ID.
     * @param accountId The ID of the account whose messages are to be retrieved.
     * @return A ResponseEntity containing the list of messages associated with the account.
     */
    public ResponseEntity<List<Message>> getMessagesByAccountId(Integer accountId) {
        List<Message> messages = messageRepository.findByPostedBy(accountId);
        return ResponseEntity.ok(messages);
    }
    /**
     * Helper method to retrieve the username of an account by its ID.
     * @param accountId The account ID to fetch the username for.
     * @return The username of the account or null if not found.
     */
    public String getUsernameByAccountId(Integer accountId) {
        // Find the account by its ID
        Account account = accountRepository.findByAccountId(accountId);

        // Return the username if the account is found
        return account != null ? account.getUsername() : null;
    }
    /**
     * Method to update the text of an existing message.
     * Validates the new message text before updating and saving the message.
     * @param messageId The ID of the message to update.
     * @param newMessageText The new message text to be set.
     * @return A ResponseEntity with the result of the update operation.
     */
    public ResponseEntity<Object> updateMessageText(Integer messageId, Message newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
    
        // If message is not found, return a bad request response
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found");
        }
        Message message = optionalMessage.get();

        // Validate that the new message text is not empty and does not exceed 255 characters
        if (newMessageText.getMessageText() == null || newMessageText.getMessageText().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Message text cannot be empty");
        }
        if (newMessageText.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Message too long: it must have a length of at most 255 characters");
        }
    
        // Update the message text and save the updated message
        message.setMessageText(newMessageText.getMessageText());
        messageRepository.save(message);
    
        // Return success response
        return ResponseEntity.ok().body("1"); // "1" indicates one row updated
    }
    /**
     * Method to delete a message by its ID.
     * Checks if the message exists, deletes it, and returns a success response.
     * @param messageId The ID of the message to delete.
     * @return A ResponseEntity indicating the result of the deletion.
     */
    public ResponseEntity<Object> deleteMessageById(Integer messageId) {
        // Check if the message exists before attempting to delete
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return ResponseEntity.ok().body("1"); // Success response, "1" indicates one row affected
        } else {
            return ResponseEntity.ok().build(); // No content if message not found
        }
    }
    /* 
    public boolean deleteMessage(int messageId) {
        if (!messageRepository.existsById(messageId)) {
            return false; // message not found
        }
        messageRepository.deleteById(messageId);
        return true; // success
    }
    
    public int updateMessage(int messageId, Message updatedMessage) throws MessageNotFoundException {
        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException("Message not found.");
        }
    
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found."));
        existingMessage.setMessageText(updatedMessage.getMessageText());
        messageRepository.save(existingMessage);
        return 1; // 1 row updated
    }
    */
}