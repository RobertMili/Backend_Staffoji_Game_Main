package com.example.backend_staffoji_game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "All details about the user level entity. ")
@Table(name = "UserScores")
@Data
public class UserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(description = "User Name")
    @Column(name = "userName", nullable = false, columnDefinition = "TEXT")
    private String userName;

    @Schema(description = "User Level")
    private int level_one;

    @Schema(description = "User Level")
    private int level_two;

    @Schema(description = "User Level")
    private int level_three;

    //todo use this if need to add more levels
//    @Schema(description = "User Level")
//    private Integer level_four;


}
