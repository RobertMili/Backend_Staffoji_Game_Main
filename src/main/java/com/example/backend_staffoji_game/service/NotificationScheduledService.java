package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NotificationScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduledService.class);
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    public List<Notification> notificationsForTodayArray = new ArrayList<>();
    private final String startCronAtMidnight = "10 0 0 * 1-12 1-5";
    private final String startCron = "0 * * * 1-12 1-5";


    public NotificationScheduledService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = startCron)
    public void checkAndSendNotifications() {
        Iterator<Notification> iterator = notificationsForTodayArray.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            isNotificationTime(notification, iterator);
        }
    }

    private void isNotificationTime(Notification notification, Iterator<Notification> iterator) {
        LocalDateTime notificationTime = notification.getSendTime();

        if (isTimeToNotify(notificationTime)) {
            sendNotification(notification);
            iterator.remove();
        }
    }

    boolean isTimeToNotify(LocalDateTime notificationTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.getHour() == notificationTime.getHour() && currentDateTime.getMinute() == notificationTime.getMinute();
    }

    void sendNotification(Notification notification) {
        NotificationDto notificationDTO = new NotificationDto(
                notification.getTitle(),
                notification.getMessage(),
                notification.getNotificationTarget(),
                notification.getSendTime(),
                notification.isSendNow()
        );

        sendingNotification(notificationDTO);
    }

    private void sendingNotification(NotificationDto notificationDTO) {
        try {
            sendingNotificationWithEmail(notificationDTO);
        } catch (RuntimeException e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void sendingNotificationWithEmail(NotificationDto NotificationDto) throws RuntimeException {
        EmailNotificationSendNowDto emailNotificationSendNowDto = new EmailNotificationSendNowDto(
                NotificationDto.getTitle(),
                NotificationDto.getMessage(),
                NotificationDto.getNotificationTarget());
        emailService.sendNotification(emailNotificationSendNowDto);
    }


    @Scheduled(cron = startCronAtMidnight)
    public void fetchingFromDatabaseInMidnight() {
        clearNotificationsArray();
        sleep10Second();
        fetchingNotificationsMidnight();
    }

    private void clearNotificationsArray() {
        //clear the list to save on memory
        notificationsForTodayArray.clear();
    }

    private static void sleep10Second() {
        try {
            // Pause for 10 seconds
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        }
    }

    private void fetchingNotificationsMidnight() {
        LocalDateTime dayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<Notification> findAll = notificationRepository.findNotificationByDate(dayStart, dayEnd);

        notificationsForTodayArray.addAll(findAll);
    }

    @EventListener
    public void handleNotificationSavedEvent(Notification notification) {
        if (isToday(notification.getSendTime())) {
            notificationsForTodayArray.add(notification);
        }
    }

    private boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now();
        return dateTime.toLocalDate().isEqual(today);
    }
}
