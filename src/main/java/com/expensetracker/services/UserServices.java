package com.expensetracker.services;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user if the email and mobile number are unique.
     *
     * @param user User object containing user details.
     * @return true if the user is created successfully, false otherwise.
     */
    public boolean createUser(User user) {
        if (isEmailOrMobileExists(user.getEmail(), user.getMobileNumber())) {
            return false; // Email or mobile number already exists.
        }
        userRepository.save(user);
        return true;
    }

    /**
     * Retrieve a user by their ID.
     *
     * @param userId ID of the user to be retrieved.
     * @return User object if found, otherwise null.
     */
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    /**
     * Check if the email or mobile number already exists in the database.
     *
     * @param email User's email.
     * @param mobileNumber User's mobile number.
     * @return true if email or mobile number exists, false otherwise.
     */
    private boolean isEmailOrMobileExists(String email, String mobileNumber) {
        return userRepository.existsByEmailOrMobileNumber(email, mobileNumber);
    }
}
