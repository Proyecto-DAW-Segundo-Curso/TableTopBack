package com.example.TableTop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.TableTop.model.User;
import com.example.TableTop.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Lógica para registrar un usuario
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        // Lógica para autenticar al usuario
        return userRepository.findByUsername(username);
    }
} 