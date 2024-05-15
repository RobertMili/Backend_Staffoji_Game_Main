package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.UserScoreDTO;
import com.example.backend_staffoji_game.service.UserScoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile({"local","dev"})
@Validated
@RequestMapping("/leaderboard")
public class UserScoreController {

    private final UserScoreService userScoreService;

    public UserScoreController(UserScoreService userScoreService) {
        this.userScoreService = userScoreService;
    }

    @Operation(summary = "choice which level to update and the score to update.")
    @PutMapping("/")
    public ResponseEntity<UserScoreDTO> createNotification(@Valid final @RequestParam String userName, @RequestParam int level, @RequestParam int score) {
        return new ResponseEntity<>( userScoreService.updateUserScore(userName,level,score), ResponseEntity.ok("Email sent successfully").getStatusCode());
    }
}
