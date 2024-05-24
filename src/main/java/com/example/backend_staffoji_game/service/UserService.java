package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.UserAdminDTO;
import com.example.backend_staffoji_game.dto.UserAdminUpdateDTO;
import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.dto.UserPremiumStatusDto;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.model.User;
import com.example.backend_staffoji_game.model.UserScore;
import com.example.backend_staffoji_game.repository.UserRepository;
import com.example.backend_staffoji_game.repository.UserScoreRepository;
import dto.UserLoginDTO;
import lombok.AllArgsConstructor;

import org.apache.commons.validator.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserScoreRepository userScoreRepository;


    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        validateUser(userDto);
        checkIfUserExists(userDto);
        checkIfEmailExists(userDto);
        User user = buildUser(userDto);
        var userScore = creatingScoreForUser(user);
        userRepository.save(user);
        userScoreRepository.save(userScore);
        return userDto;
    }

    private static void validateEmail(UserDto userDto) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(userDto.getEmail())) {
            throw new UserDoesNotExistsException("Invalid email address");
        }
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
                .isAdmin(false)
                .build();
        return user;
    }

    private UserScore creatingScoreForUser(User user) {
        UserScore userScore = new UserScore();
        userScore.setUserName(user.getUsername());
        userScore.setLevel_one(0);
        userScore.setLevel_two(0);
        userScore.setLevel_three(0);
        return userScore;
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

    public UserAdminDTO getUserByUsernameAndPassword(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        if (isUserExists(user)) {
            return new UserAdminDTO(user.getUsername(), user.getPassword(),user.getEmail(), user.isPremium(), user.isAdmin());
        } else {
            throw new UserDoesNotExistsException("User not found with the provided email and password");
        }
    }

    private static boolean isUserExists(User user) {
        return user != null;
    }

    public UserAdminUpdateDTO updateIsAdmin(UserAdminUpdateDTO userDto) {
        var user = findUserByUsername(userDto.getUsername());
        user.setAdmin(userDto.isAdmin());
        user.setPremium(userDto.isPremium());
        userRepository.save(user);
        return userDto;
    }
}
