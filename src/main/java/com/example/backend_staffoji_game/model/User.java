package com.example.backend_staffoji_game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "All details about the step entity. ")
@Table(name = "Users")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The database generated user ID")
    private long id;

    @Column(unique = true, name = "username")
    @Schema(description = "User Id")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(unique = true,name = "email")
    private String email;

    @Schema(description = "Premium status of the user")
    @Column(name = "is_premium", nullable = false, columnDefinition = "bit default 0")
    private boolean isPremium;
}
