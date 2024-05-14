package com.example.backend_staffoji_game.service;

import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.exception.UserAlreadyExistsException;
import com.example.backend_staffoji_game.exception.UserDoesNotExistsException;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.repository.NotificationRepository;
import com.example.backend_staffoji_game.service.sendingEmail.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;


    public NotificationDto createNotification(NotificationDto notificationDto) {
        validateNotification(notificationDto);
        checkIfTitleExists(notificationDto);
        checkIfIsTimeInFuture(notificationDto);

        var emailNotificationSendNowDto = createEmailNotificationDTO(notificationDto);
        sendEmailNotification(emailNotificationSendNowDto);

        Notification notification = buildNotification(notificationDto);
        notificationRepository.save(notification);

        return notificationDto;
    }

    private void validateNotification(NotificationDto notificationDto) {
        if (notificationDto.getTitle() == null || notificationDto.getTitle().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        if (notificationDto.getMessage() == null || notificationDto.getMessage().isEmpty()) {
            throw new RuntimeException("Message is required");
        }
    }

    private void checkIfTitleExists(NotificationDto notificationDto) {
        var notification = notificationRepository.findByTitle(notificationDto.getTitle());
        if (notification.isPresent()) {
            throw new UserDoesNotExistsException("Notification with this title already exists");
        }
    }

    private void checkIfIsTimeInFuture(NotificationDto notificationDto) {
        LocalDateTime now = LocalDateTime.now();
        if (notificationDto.getSendTime() != null && (!notificationDto.isSendNow() && notificationDto.getSendTime().isBefore(now))) {
            throw new UserAlreadyExistsException("Send time must be in the future");
        }
    }

    private static EmailNotificationSendNowDto createEmailNotificationDTO(NotificationDto notificationDto) {
        var emailNotificationSendNowDto = new EmailNotificationSendNowDto(notificationDto.getTitle(), notificationDto.getMessage(), notificationDto.getNotificationTarget());
        return emailNotificationSendNowDto;
    }

    private void sendEmailNotification(EmailNotificationSendNowDto emailNotificationSendNowDto) {
        boolean isEmailSend = emailService.sendNotification(emailNotificationSendNowDto);
        if (!isEmailSend) {
            throw new UserDoesNotExistsException("Failed to send email");
        }
    }


    private Notification buildNotification(NotificationDto notificationDto) {
        Notification notification = Notification.builder()
                .title(notificationDto.getTitle())
                .message(notificationDto.getMessage())
                .notificationTarget(notificationDto.getNotificationTarget())
                .sendNow(notificationDto.isSendNow())
                .sendTime(notificationDto.getSendTime())
                .build();
        return notification;
    }

    public List<Notification> getAllNotifications() {
        var findAll = notificationRepository.findAll();
        checkIfNotificationsExist(findAll);
        return findAll;
    }

    private void checkIfNotificationsExist(List<Notification> findAll) {
        if (findAll.isEmpty()) {
            throw new UserDoesNotExistsException("No notifications found");
        }
    }
}
