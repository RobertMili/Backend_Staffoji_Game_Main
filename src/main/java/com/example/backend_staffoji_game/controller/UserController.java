package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.UserDto;
import com.example.backend_staffoji_game.dto.UserPremiumStatusDto;
import com.example.backend_staffoji_game.model.User;
import com.example.backend_staffoji_game.service.UserService;
import dto.UserLoginDTO;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
@RestController
@Profile({"local","dev"})
@Validated
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/")
    public ResponseEntity<UserDto> createNotification(@Valid final @RequestBody UserDto userDto) {
        return new ResponseEntity<>( userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return new ResponseEntity<>(userService.getUserByUsernameAndPassword(userLoginDTO.getUsername(),userLoginDTO.getPassword()), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<UserPremiumStatusDto> updateUser(@Valid final @RequestBody UserPremiumStatusDto userDto) {
        return new ResponseEntity<>(userService.updateIsPremium(userDto), HttpStatus.OK);
    }

}
