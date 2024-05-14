package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationScheduledService {

    private EmailService emailService;
    private final NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    public List<Notification> notificationsForTodayArray = new ArrayList<>();
    public Set<Integer> checkIfNotificationIdsAlreadySent = new HashSet<>();
    private final String startCron = "0 * * * 1-12 1-5";
    private final String startCronAtMidnight = "10 0 0 * 1-12 1-5";
    //todo delete this one:
    private final String startCronEvery30Sec = "0/30 * * * * *";

    public NotificationScheduledService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }


    @Scheduled(cron = startCronEvery30Sec)
    public void checkAndSendNotifications() {
        System.out.println("Notifications for today array: " + notificationsForTodayArray);
        notificationsForTodayArray.forEach(this::sendNotificationIfTImeMatches);

    }

    private void sendNotificationIfTImeMatches(Notification notification) {
        LocalDateTime notificationTime = notification.getSendTime();


        //todo delete this one
        System.out.println("sending notification if time matches");

        if (isTimeToNotify(notificationTime) && isNewNotification(notification)) {
            sendNotification(notification);
            checkIfNotificationIdsAlreadySent.add(notification.getNotificationId());
        }
    }

    private boolean isTimeToNotify(LocalDateTime notificationTime) {
        System.out.println("coming here send isTimeToNotify");
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.getHour() == notificationTime.getHour() && currentDateTime.getMinute() == notificationTime.getMinute();
    }

    private boolean isNewNotification(Notification notification) {
        System.out.println("coming here send isNewNotification");
        return !checkIfNotificationIdsAlreadySent.contains(notification.getNotificationId());
    }

    private void sendNotification(Notification notification) {
        System.out.println("coming here send notification");
        NotificationDto notificationDTO = new NotificationDto(notification.getTitle(), notification.getMessage(),
                notification.getNotificationTarget(), notification.getSendTime(), notification.isSendNow());

        sendingNotification(notificationDTO);
    }

    private void sendingNotification(NotificationDto notificationDTO) {
        try {
            logger.info("Sending notification: {}", notificationDTO.toString());
            sendingNotificationWithEmail(notificationDTO);
        } catch (UserAlreadyExistsException e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void sendingNotificationWithEmail(NotificationDto notificationDTO) {
        System.out.println("coming here");
        if (Boolean.TRUE.equals(notificationDTO.isSendNow())) {
            EmailNotificationSendNowDto emailNotificationSendNowDto = new EmailNotificationSendNowDto(notificationDTO.getTitle(), notificationDTO.getMessage(), notificationDTO.getNotificationTarget());
            emailService.sendNotification(emailNotificationSendNowDto);
        }
    }

    //todo delete this one
    @Scheduled(cron = startCronEvery30Sec)
    public void fetchingFromDatabaseInMidnight() {
        System.out.println("checking crone");
        clearNotificationsList();
        fetchingNotificationsMidnight();
    }

    private void clearNotificationsList() {
        //clear the list to save on memory
        notificationsForTodayArray.clear();
        checkIfNotificationIdsAlreadySent.clear();
    }

    private void fetchingNotificationsMidnight() {
        LocalDateTime dayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        //todo gona fix later
//        List<Notification> findAll = notificationRepository.findNotificationByDate(dayStart, dayEnd);
//
//        notificationsForTodayArray.addAll(findAll);
    }

    @EventListener
    public  void handleNotificationSavedEvent(Notification notification) {
        synchronized (this) {
            if (isToday(notification.getSendTime())) {
                System.out.println("addet to today array " + notification);
                notificationsForTodayArray.add(notification);
                System.out.println("Notifications for today: " + notificationsForTodayArray);
            }
        }
    }

    private boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now();
        return dateTime.toLocalDate().isEqual(today);
    }

}
