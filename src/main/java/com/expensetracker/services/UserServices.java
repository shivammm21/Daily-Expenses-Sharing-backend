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

    public boolean createUser(User user) {
        if (isEmailOrMobileExists(user.getEmail(), user.getMobileNumber())) {
            return false; // Email or mobile number already exists.
        }
        userRepository.save(user);
        return true;
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    private boolean isEmailOrMobileExists(String email, String mobileNumber) {
        return userRepository.existsByEmailOrMobileNumber(email, mobileNumber);
    }
}
