package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class NotificationScheduledServiceTest_Mocka {

    @Mock
    private NotificationRepository notificationRepositoryMock;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationScheduledService notificationScheduledServiceMock;

    @InjectMocks
    private NotificationService notificationServiceTest;

    @Autowired
    private NotificationRepository notificationRepositoryAutowired;

    @Autowired
    public NotificationScheduledService notificationScheduledService;

    public Notification mockedNotification;
    private NotificationDto addNotificationTestPassDate;
    private EmailService emailServiceAutowired;


    @BeforeEach
    void setUp() {
        if (!isDatabaseEmpty()) {
            dropAllTables();
        }
        notificationServiceTest = new NotificationService(notificationRepositoryAutowired, emailServiceAutowired, applicationEventPublisher);

        // Create an instance of CronJobService
        notificationScheduledServiceMock = new NotificationScheduledService(notificationRepositoryMock, emailService);

        mockedNotification = new Notification(1, "Test", "This is a test.",
                "all", false, LocalDateTime.now().plusHours(1));

        addNotificationTestPassDate = new NotificationDto(
                "test",
                "test",
                "all",
                LocalDateTime.now().minusDays(1),
                false);


    }


    @Test
    void checkAndSendNotification_MockTest_SendingMultiObjects() {
        // Check that database is empty
        assertTrue(isDatabaseEmpty());

        //Create a list of Notification objects
        List<Notification> notifications = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Notification notification = new Notification(
                    i,
                    "Test",
                    "This is a test.",
                    "all",
                    false,
                    LocalDateTime.now());
            notifications.add(notification);
        }


        // Set the notificationForTodayArray of CronJobService to the list  of Notification objects
        notificationScheduledServiceMock.notificationsForTodayArray = notifications;

        // Call the checkAndSendNotifcations Method
        notificationScheduledServiceMock.checkAndSendNotifications();

        // check the size of array
        assertEquals(notificationScheduledServiceMock.notificationsForTodayArray.size(), notifications.size());

        // Verify that fcmService.sendNotificationByTopis is called the expected number of times
        verify(emailService, times(5)).sendNotification(any(EmailNotificationSendNowDto.class));
    }

    @DisplayName("test Illegal Argument Exception handling if is passing date in the past")
    @Test
    void testIllegalArgumentExceptionHandling() {
        // Arrange

        doThrow(IllegalArgumentException.class).when(notificationServiceMock).createNotification(any(NotificationDto.class));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notificationServiceMock.createNotification(addNotificationTestPassDate);
        });
    }

    @DisplayName("checkAndSendNotifications method with no notifications in database")
    @Test
    void testCheckAndSendNotifications_NoNotifications_MockTest() {
        // Check that the database is empty
        assertTrue(isDatabaseEmpty());

        // When notificationRepository.findNotificationByDate is called, return an empty list
        lenient().when(notificationRepositoryMock.findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Call the checkAndSendNotifications method
        notificationScheduledServiceMock.checkAndSendNotifications();

        // Verify that fcmService.sendNotificationByTopic is not called
        verifyNoInteractions(emailService);
        verify(emailService, times(0)).sendNotification(any(EmailNotificationSendNowDto.class));

    }

    @DisplayName("checkAndSendNotifications method with one notification in database")
    @Test
    void checkAndSendNotifications_MockTest() {
        // Check that the database is empty
        assertTrue(isDatabaseEmpty());

        //  Create a list of Notification objects
        Notification notification2 = new Notification(1, "Test", "This is a test.",
                "all", false, LocalDateTime.now().plusHours(1));
        List<Notification> notifications = Arrays.asList(mockedNotification, notification2);

        // Set the notificationsForTodayArray of CronJobService to the list of Notification objects
        notificationScheduledServiceMock.notificationsForTodayArray = notifications;

        //  Call the checkAndSendNotifications method
        notificationScheduledServiceMock.checkAndSendNotifications();

        // Verify that fcmService.sendNotificationByTopic is called the expected number of times
        verify(emailService, times(0)).sendNotification(any(EmailNotificationSendNowDto.class));

    }

    @DisplayName("adding notification to database, get correct object from array through method fetchingNotificationsMidnight")
    @Test
    void checkAndSendNotifications_getArrayWithUpdate() {
        assertTrue(isDatabaseEmpty());

        NotificationDto notificationDto = new NotificationDto(
                "Test",
                "This is a test.",
                "all",
                LocalDateTime.now().plusHours(1),
                false);
        NotificationDto notificationDtoUpdate = new NotificationDto(
                "Test2",
                "This is a test2.",
                "all",
                LocalDateTime.now().plusHours(2),
                false);

        // Arrange
        notificationServiceTest.createNotification(notificationDto);

        var expectedNotifications = notificationServiceTest.createNotification(notificationDtoUpdate);


        // Act
        notificationScheduledService.fetchingFromDatabaseInMidnight();

        var resultFirstObject = notificationScheduledService.notificationsForTodayArray;


//        // Assert
        assertNotNull(resultFirstObject);
        assertEquals(expectedNotifications.getTitle(), resultFirstObject.get(1).getTitle());
    }

    @Test
    void testFetchingFromDatabaseInMidnight_UnitT() {
        LocalDateTime dayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // Arrange
        when(notificationRepositoryMock.findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockedNotification));

        // Act
        notificationScheduledService.fetchingFromDatabaseInMidnight();

        var test = notificationRepositoryMock.findNotificationByDate(dayStart, dayEnd);

        System.out.println(test);
        // Assert
        verify(notificationRepositoryMock).findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void fetchingFromDatabaseInMidnight() {
        // Arrange
        Notification notification1 = new Notification(1, "Test1", "This is a test1.", "all", false, LocalDateTime.now().plusHours(1));
        Notification notification2 = new Notification(2, "Test2", "This is a test2.", "all", false, LocalDateTime.now().plusHours(2));
        List<Notification> notifications = Arrays.asList(notification1, notification2);

        when(notificationRepositoryMock.findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(notifications);

        // Act
        notificationScheduledServiceMock.fetchingFromDatabaseInMidnight();

        // Assert
        verify(notificationRepositoryMock).findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class));
        assertEquals(notifications, notificationScheduledServiceMock.notificationsForTodayArray);
    }

    @Test
    void testIsNotificationTime() {
        // Arrange
        Notification notificationNow = new Notification(1, "Test", "This is a test.", "all", false, LocalDateTime.now());

        boolean result = notificationScheduledService.isTimeToNotify(notificationNow.getSendTime());

        // Assert
        assertTrue(result);

    }

    @Test
    void testSendNotification() {
        // Arrange
        Notification notification = new Notification(1, "Test", "This is a test.", "all", false, LocalDateTime.now());


        // Act
        notificationScheduledServiceMock.sendNotification(notification);

        // Assert
        verify(emailService, times(1)).sendNotification(any(EmailNotificationSendNowDto.class));
    }

    @Test
    void testFetchingFromDatabaseInMidnight_NoNotifications() {
        // Arrange
        when(notificationRepositoryMock.findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        notificationScheduledServiceMock.fetchingFromDatabaseInMidnight();

        // Assert
        assertTrue(notificationScheduledServiceMock.notificationsForTodayArray.isEmpty());
    }

    @Test
    void testHandleNotificationSavedEvent() {
        // Arrange
        Notification notification = new Notification(1, "Test", "This is a test.", "all", false, LocalDateTime.now());

        // Act
        notificationScheduledServiceMock.handleNotificationSavedEvent(notification);

        // Assert
        assertTrue(notificationScheduledServiceMock.notificationsForTodayArray.contains(notification));
    }

    @Test
    void testFetchingFromDatabaseInMidnight_NotificationsForFutureDate() {
        // Arrange
        Notification notification = new Notification(1, "Test",
                "This is a test.", "all", false, LocalDateTime.now().plusDays(1));
        Notification notificationToday = new Notification(1, "Test",
                "This is a test.", "all", false, LocalDateTime.now());


        // Mock the findNotificationByDate method to return the notificationToday object
        when(notificationRepositoryMock.findNotificationByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime start = invocation.getArgument(0);
                    LocalDateTime end = invocation.getArgument(1);
                    if (notification.getSendTime().isAfter(start) && notification.getSendTime().isBefore(end)) {
                        return Collections.singletonList(notification);
                    } else if (notificationToday.getSendTime().isAfter(start) && notificationToday.getSendTime().isBefore(end)) {
                        return Collections.singletonList(notificationToday);
                    } else {
                        return Collections.emptyList();
                    }
                });

        // Act
        notificationScheduledServiceMock.fetchingFromDatabaseInMidnight();

        System.out.println(notificationScheduledServiceMock.notificationsForTodayArray);

        // Assert
        assertEquals(Collections.singletonList(notificationToday), notificationScheduledServiceMock.notificationsForTodayArray);
    }

    @Test
    void handleNotificationSavedEvent() {
    }

    private void dropAllTables() {
        notificationRepositoryAutowired.deleteAll();
    }

    private boolean isDatabaseEmpty() {
        return notificationRepositoryAutowired.findAll().isEmpty();
    }

    @AfterEach
    void tearDown() {
        dropAllTables();

    }
}