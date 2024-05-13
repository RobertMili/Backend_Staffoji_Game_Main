package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.EmailNotificationSendNowDto;
import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.service.sendingEmail.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@Profile({"local", "dev"})
@Validated
@RequestMapping("/email-notification-sending-now")
public class EmailSendingController {

    private final EmailService emailService;
    Logger logger = Logger.getLogger(EmailSendingController.class.getName());

    public EmailSendingController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Choice \"notificationTarget\": \"all\"  , \"premium\"  or \"notPremium\"")
    @PostMapping("/")
    public ResponseEntity sendEmail(@Valid final @RequestBody EmailNotificationSendNowDto emailNotificationSendNowDto) {
        boolean isEmailSent = emailService.sendNotification(emailNotificationSendNowDto);
        if (isEmailSent) {
            return ResponseEntity.ok("Email sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
}

