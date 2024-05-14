package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile({"local","dev"})
@Validated
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Operation(summary = "notificationTarget: all, premium, notPremium, sendNow: true, false")
    @PostMapping("/")
    public ResponseEntity<NotificationDto> createNotification(@Valid final @RequestBody NotificationDto notificationDto) {
        return new ResponseEntity<>( notificationService.createNotification(notificationDto), ResponseEntity.ok("Email sent successfully").getStatusCode());
    }

    @GetMapping("/")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notificationService.getAllNotifications(), HttpStatus.OK);
    }
}
