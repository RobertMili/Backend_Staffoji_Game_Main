package com.example.backend_staffoji_game.controller;

import com.example.backend_staffoji_game.dto.UserAdminDTO;
import com.example.backend_staffoji_game.dto.UserAdminUpdateDTO;
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

import java.util.List;

@CrossOrigin
@RestController
@Profile({"local", "dev"})
@Validated
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@Valid final @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAdminDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return new ResponseEntity<>(userService.getUserByUsernameAndPassword(userLoginDTO.getEmail(), userLoginDTO.getPassword()), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("/updatePremium")
    public ResponseEntity<UserPremiumStatusDto> updateUser(@Valid final @RequestBody UserPremiumStatusDto userDto) {
        return new ResponseEntity<>(userService.updateIsPremium(userDto), HttpStatus.OK);
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<UserAdminUpdateDTO> updateUser(@Valid final @RequestBody UserAdminUpdateDTO userDto) {
        return new ResponseEntity<>(userService.updateIsAdmin(userDto), HttpStatus.OK);
    }
    @DeleteMapping("/userName/{userName}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        boolean isVerified = userService.verifyUser(token);
        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token");
        }
    }

}
