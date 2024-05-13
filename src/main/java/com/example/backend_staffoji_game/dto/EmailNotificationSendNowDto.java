package com.example.backend_staffoji_game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "All details about the notification entity. ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationSendNowDto {
    @Schema(description = "Notification Title")
    private String title;
    @Schema(description = "Notification Message")
    private String message;
    @Schema(description = "Notification Target", defaultValue = "all")
    private String notificationTarget;
}
