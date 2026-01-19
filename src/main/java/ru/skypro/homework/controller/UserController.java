package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(@Valid @RequestBody NewPassword newPassword,
                                         Authentication authentication) {
        String username = authentication.getName();
        boolean success = userService.updatePassword(username, newPassword);

        return success ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Current password is incorrect");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UpdateUser updateUser,
                                           Authentication authentication) {
        String username = authentication.getName();
        return userService.updateUser(username, updateUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUserImage(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestParam("image") MultipartFile image) {
        try {
            User updatedUser = userService.updateUserImage(userDetails.getUsername(), image);
            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
