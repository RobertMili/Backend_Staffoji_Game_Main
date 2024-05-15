package com.example.backend_staffoji_game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "All details about the user score entity. ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreDTO {

    @Schema(description = "User Id")
    private long userId;

    @Schema(description = "User Level")
    private int level_one;

    @Schema(description = "User Level")
    private int level_two;

    @Schema(description = "User Level")
    private int level_three;

}
