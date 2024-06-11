package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.dto.UserScoreDTO;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.repository.UserRepository;
import com.example.backend_staffoji_game.repository.UserScoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserScoreServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserScoreRepository userScoreRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserScoreService userScoreService;


    @BeforeEach
    void setUp() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        userRepository.deleteAll();
        userScoreRepository.deleteAll();
    }

    @Test
    void createUserCheckIfUserScoreIsCreateInDatabase_positiveTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");
        var resultScore = userScoreRepository.findByUserNameIs(result.get().getUsername());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), userTest.getUsername());
        assertEquals(resultScore.get().getUserName(), userTest.getUsername());
        assertEquals(resultScore.get().getLevel_one(), 0);
        assertEquals(resultScore.get().getLevel_two(), 0);
        assertEquals(resultScore.get().getLevel_three(), 0);
    }

    @Test
    void createUserCheckIfUserScoreIsCreateInDatabase_negativeTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Check if user is saved
        var result = userRepository.findByUsername("test");
        var resultScore = userScoreRepository.findByUserNameIs(result.get().getUsername());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), userTest.getUsername());
        assertEquals(resultScore.get().getUserName(), userTest.getUsername());
        assertNotEquals(resultScore.get().getLevel_one(), 100);
        assertNotEquals(resultScore.get().getLevel_two(), 100);
        assertNotEquals(resultScore.get().getLevel_three(), 100);
    }

    @Test
    void createUserCheckIfUserScoreIsCreateInDatabase_edgeCaseTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        for (int i = 0; i < 10; i++) {
            // Create a user
            UserDto userTest = new UserDto("test" + i, "test", "test"+ i + "@gmail.com", false);

            // Save user
            userService.createUser(userTest);

        }
        // Check if user is saved
        var result = userRepository.findAll();
        var resultScoreFirst = userScoreRepository.findByUserNameIs(result.get(0).getUsername());
        var resultScoreLast = userScoreRepository.findByUserNameIs(result.get(9).getUsername());

        // Assert
        assertEquals(result.size(), 10);
        assertEquals(resultScoreFirst.get().getUserName(), "test0");
        assertEquals(resultScoreLast.get().getUserName(), "test9");
    }

    @Test
    void updateUserScore_userDoesNotExistTest() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Assert
        assertThrows(UserDoesNotExistsException.class, () -> {
            userScoreService.updateUserScore("nonexistent", 1, 50);
        });
    }

    @Test
    void updateUserScore_positiveTest() {
        //check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Update user score
        userScoreService.updateUserScore("test", 1, 50);
        userScoreService.updateUserScore("test", 2, 100);
        userScoreService.updateUserScore("test", 3, 150);

        // Check if score is updated
        var resultScore = userScoreRepository.findByUserNameIs("test");

        // Assert
        assertTrue(resultScore.isPresent());
        assertEquals(resultScore.get().getLevel_one(), 50);
        assertEquals(resultScore.get().getLevel_two(), 100);
        assertEquals(resultScore.get().getLevel_three(), 150);
    }

    @Test
    void updateUserScore_twice_positiveTest() {
        //check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Update user score
        userScoreService.updateUserScore("test", 1, 50);
        userScoreService.updateUserScore("test", 2, 100);
        userScoreService.updateUserScore("test", 3, 150);

        // Update score second time
        userScoreService.updateUserScore("test", 1, 50);
        userScoreService.updateUserScore("test", 2, 100);
        userScoreService.updateUserScore("test", 3, 150);

        // Check if score is updated
        var resultScore = userScoreRepository.findByUserNameIs("test");

        // Assert
        assertTrue(resultScore.isPresent());
        assertEquals(resultScore.get().getLevel_one(), 100);
        assertEquals(resultScore.get().getLevel_two(), 200);
        assertEquals(resultScore.get().getLevel_three(), 300);
    }

    @Test
    void createUserAndSetAllToNUll() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test@gmail.com", false);

        // Save user
        userService.createUser(userTest);

        // Update user score
        userScoreService.updateUserScore("test", 1, 50);
        userScoreService.updateUserScore("test", 2, 100);
        userScoreService.updateUserScore("test", 3, 150);

        // Delete user score
        userScoreService.deletingUserScore(userTest.getUsername());

        // expected
        var expected = userScoreRepository.findByUserNameIs("test");

        // Assert
        assertEquals(expected.get().getLevel_one(), 0);
        assertEquals(expected.get().getLevel_two(), 0);
        assertEquals(expected.get().getLevel_three(), 0);
    }

    @Test
    void verificationEmail() {
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
        assertEquals(result.get().isEmailVerified(), false);

        // Verify email
        userService.verifyUser(result.get().getVerificationToken());

        var newResult = userRepository.findByUsername("test");

        // Check if user is saved
        assertEquals(newResult.get().isEmailVerified(), true);
    }

    @Test
    void createUserWithFailEmail() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // Create a user
        UserDto userTest = new UserDto("test", "test", "test", false);

        // Assert
        assertThrows(UserDoesNotExistsException.class, () -> {
            userService.createUser(userTest);
        });

    }
    private boolean databaseIsEmpty() {
        userRepository.findAll().isEmpty();
        userScoreRepository.findAll().isEmpty();
        return true;
    }


}