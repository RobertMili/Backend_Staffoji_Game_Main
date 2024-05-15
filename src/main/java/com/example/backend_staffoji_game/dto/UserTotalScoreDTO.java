package com.example.backend_staffoji_game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "All details about the user score entity without the userId. ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTotalScoreDTO {

    @Schema(description = "User Name")
    private String userName;

    @Schema(description = "Total Score")
    private int totalScore;
}
