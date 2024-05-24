package com.example.backend_staffoji_game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminUpdateDTO {
    private String username;
    private boolean isPremium;
    private boolean isAdmin;
}
