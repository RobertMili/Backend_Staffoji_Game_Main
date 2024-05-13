package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.dto.UserPremiumStatusDto;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.model.User;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import com.example.backend_staffoji_game.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        checkIfUserExists(userDto);
        checkIfEmailExists(userDto);
        User user = buildUser(userDto);
        userRepository.save(user);
        return userDto;
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
    }

    private void checkIfUserExists(UserDto userDto) {
        var user = userRepository.findByUsername(userDto.getUsername());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    private void checkIfEmailExists(UserDto userDto) {
        var email = userRepository.findByEmail(userDto.getEmail());
        if (email.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
    }

    private static User buildUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .email(userDto.getEmail())
                .isPremium(userDto.isPremium())
                .build();
        return user;
    }

    public List<User> getAllUsers() {
        var findAll = userRepository.findAll();
        checkIfUsersExist(findAll);
        return findAll;
    }

    private static void checkIfUsersExist(List<User> findAll) {
        if (findAll.isEmpty()) {
            throw new UserDoesNotExistsException("No users found");
        }
    }

    public UserPremiumStatusDto updateIsPremium(UserPremiumStatusDto userDto) {
        var user = findUserByUsername(userDto.getUsername());
        user.setPremium(userDto.isPremium());
        userRepository.save(user);
        return userDto;

    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserDoesNotExistsException("User not found"));
    }

}
