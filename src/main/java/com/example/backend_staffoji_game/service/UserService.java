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
import lombok.AllArgsConstructor;
import org.apache.commons.validator.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserScoreRepository userScoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private String baseUrl;

    public UserService(UserRepository userRepository, UserScoreRepository userScoreRepository, PasswordEncoder passwordEncoder, EmailService emailService, @Value("${APP_BASE_URL}") String baseUrl) {
        this.userRepository = userRepository;
        this.userScoreRepository = userScoreRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.baseUrl = baseUrl;
    }

    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto);
        validateUser(userDto);
        checkIfUserExists(userDto);
        checkIfEmailExists(userDto);
        User user = buildUser(userDto);
        generateAndSendVerificationToken(user);
        var userScore = creatingScoreForUser(user);
        userRepository.save(user);
        userScoreRepository.save(userScore);
        return userDto;
    }

    private void saveUserVerificationToken(User user, String token) {
        user.setVerificationToken(token);
        userRepository.save(user);
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

    private User buildUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .isPremium(userDto.isPremium())
                .isAdmin(false)
                .build();
        return user;
    }

    private void generateAndSendVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        saveUserVerificationToken(user, token);
        String verificationLink = baseUrl + "/verify?token=" + token;

        emailService.sendVerificationEmail(user.getEmail(),
                "Please verify your email",
                "Click the following link to verify your email: "
                        + verificationLink);
    }

    public boolean verifyUser(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmailVerified(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
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

    public UserAdminDTO getUserByUsernameAndPassword(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (isUserExists(rawPassword, user)) {
            verifyUserEmailStatus(user);
            return new UserAdminDTO(user.get().getUsername(), user.get().getPassword(), user.get().getEmail(), user.get().isPremium(), user.get().isAdmin());
        } else {
            throw new UserDoesNotExistsException("User not found with the provided email and password");
        }
    }

    private static void verifyUserEmailStatus(Optional<User> user) {
        if (!user.get().isEmailVerified()) {
            throw new UserDoesNotExistsException("Email not verified");
        }
    }

    private boolean isUserExists(String rawPassword, Optional<User> user) {
        return user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword());
    }

    public UserAdminUpdateDTO updateIsAdmin(UserAdminUpdateDTO userDto) {
        var user = findUserByUsername(userDto.getUsername());
        user.setAdmin(userDto.isAdmin());
        user.setPremium(userDto.isPremium());
        userRepository.save(user);
        return userDto;
    }

    public void deleteUser(String userName) {
        var user = findUserByUsername(userName);
        var userScore = userScoreRepository.findByUserNameIs(userName)
                .orElseThrow(() -> new UserDoesNotExistsException("No UserScore found with id: " + userName));;
        userRepository.delete(user);
        userScoreRepository.delete(userScore);
    }
}