package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import com.example.backend_staffoji_game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EmailServiceTest_IntegrationTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private NotificationDto notificationObject;

    LocalDateTime now = LocalDateTime.now();


    @BeforeEach
    public void setUp() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createNotification_testingAll() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // add user
        addingUser();

        // Create a notification
        notificationObject = new NotificationDto("test", "test", "all", now, true);

        // Save user
        notificationService.createNotification(notificationObject);

        // Check if user is saved
        var result = notificationRepository.findByTitle(notificationObject.getTitle());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getNotificationTarget(), notificationObject.getNotificationTarget());
    }

    @Test
    void createNotification_testingPremium() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // add user
        addingUser();

        // Create a notification
        notificationObject = new NotificationDto("test", "test", "premium", now, true);

        // Save user
        notificationService.createNotification(notificationObject);

        // Check if user is saved
        var result = notificationRepository.findByTitle(notificationObject.getTitle());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getNotificationTarget(), notificationObject.getNotificationTarget());
    }

    @Test
    void createNotification_testingNotPremium() {
        // Check if database is empty
        assertTrue(databaseIsEmpty());

        // add user
        addingUser();

        // Create a notification
        notificationObject = new NotificationDto("test", "test", "notPremium", now, true);

        // Save user
        notificationService.createNotification(notificationObject);

        // Check if user is saved
        var result = notificationRepository.findByTitle(notificationObject.getTitle());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getNotificationTarget(), notificationObject.getNotificationTarget());
    }

    private void addingUser() {
        var userPremium = new UserDto("userPremium", "testNegative", "=robertmilicevic3869@gmail.com", true);
        var userNotPremium = new UserDto("userNotPremium", "testNegative", "staffoji_game@mail.com", false);
        userService.createUser(userPremium);
        userService.createUser(userNotPremium);
    }

    private boolean databaseIsEmpty() {
        return notificationRepository.findAll().isEmpty();
    }
}
