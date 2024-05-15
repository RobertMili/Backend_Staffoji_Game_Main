package com.example.backend_staffoji_game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "All details about the step entity. ")
@Table(name = "Notification")
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The database generated notification ID")
    private int notificationId;

    @Schema(description = "Title")
    @Column(name = "title", nullable = false)
    private String title;

    @Schema(description = "Message")
    @Column(name = "message", nullable = false)
    private String message;


    @Schema(description = "Notification Target")
    @Column(name = "notification_target", nullable = false)
    private String notificationTarget;


    @Schema(description = "Topic")
    @Column(name = "topic", nullable = false)
    private boolean sendNow;


    @Schema(description = "Send Time")
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

}
