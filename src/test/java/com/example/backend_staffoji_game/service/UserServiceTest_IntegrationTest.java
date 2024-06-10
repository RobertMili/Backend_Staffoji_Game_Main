package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.UserAdminUpdateDTO;
import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.dto.UserPremiumStatusDto;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.repository.UserRepository;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserServiceTest_IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUp() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        userRepository.deleteAll();
    }


    @Test
    void createUser_positiveTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), userTest.getUsername());
        assertTrue(passwordEncoder.matches(userTest.getPassword(), result.get().getPassword()));
        assertEquals(result.get().getEmail(), userTest.getEmail());
    }

    @Test
    void createUser_negativeTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("testNegative", "testNegative", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Try to create another user with the same username and email
        UserDto duplicateUser = new UserDto("testNegative", "testNegative", "test@gmail.com", false);

        // Assert that a UserAlreadyExistsException is thrown
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    void createUser_edgeCaseTest_checkingToAvoidRaceCondition() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user and save it 100 times
        for (int i = 0; i < 100; i++) {
            // Create a user
            UserDto userTest = new UserDto("test" + i, "test" + i, "test" + i + "@gmail.com",false);
            // Save user
            userService.createUser(userTest);
        }

        // Check if user is saved
        var result = userRepository.findAll();

        // Assert
        assertEquals(result.size(), 100);
    }

    @Test
    void createUser_checkingIsPremiumFalseDefault() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com");

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().isPremium(),false);
    }

    @Test
    void checkingIsPremiumTrue() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", true);

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().isPremium(), userTest.isPremium());
    }

    @Test
    void createUser_UpdateToTrue() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com");
        UserPremiumStatusDto updateUser = new UserPremiumStatusDto(userTest.getUsername(), true);

        // Save user
        userService.createUser(userTest);

        //update user to premium
        userService.updateIsPremium(updateUser);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().isPremium(),true);
    }

    @Test
    void checkIfUserExists() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        var expect = userService.getUserByUsernameAndPassword(userTest.getEmail(), userTest.getPassword());

        // Assert;
        assertEquals(expect.getEmail(), userTest.getEmail());
        assertTrue(passwordEncoder.matches(userTest.getPassword(), expect.getPassword()));
    }

    @Test
    void getUserByUsernameANdPassword_negativeTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        assertThrows(UserDoesNotExistsException.class, () -> {
            userService.getUserByUsernameAndPassword("nonexistent", "nonexistent");
        });
    }

    @Test
    void getUserByUsernameAndPassword_nullUsernameTest(){
        assertThrows(UserDoesNotExistsException.class, () -> {
            userService.getUserByUsernameAndPassword(null, "nonexistent");
        });
    }

    @Test
    void getUserByUsernameAndPassword_nullPasswordTest(){
        assertThrows(UserDoesNotExistsException.class, () -> {
            userService.getUserByUsernameAndPassword("nonexistent", null);
        });
    }

    @Test
    void createUserWithAdminFalse_positiveTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().isAdmin(),false);
    }

    @Test
    void updateAdmin() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);
        UserAdminUpdateDTO updateUser = new UserAdminUpdateDTO(userTest.getUsername(), true,true);

        // Save user
        userService.createUser(userTest);
        userService.updateIsAdmin(updateUser);

        // Check if user is saved
        var result = userRepository.findByUsername("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), userTest.getUsername());
        assertEquals(result.get().isAdmin(),true);
        assertEquals(result.get().isPremium(),true);

    }

    @Test
    void getAllUsers() {
    }



    private boolean databaseIsEmpty() {
        return userRepository.findAll().isEmpty();
    }

}