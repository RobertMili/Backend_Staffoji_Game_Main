package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.NotificationDto;
import com.example.backend_staffoji_game.model.Notification;
import com.example.backend_staffoji_game.service.NotificationService;
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


    @PostMapping("/")
    public ResponseEntity<NotificationDto> createNotification(@Valid final @RequestBody NotificationDto notificationDto) {
        return new ResponseEntity<>( notificationService.createNotification(notificationDto), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notificationService.getAllNotifications(), HttpStatus.OK);
    }
}
