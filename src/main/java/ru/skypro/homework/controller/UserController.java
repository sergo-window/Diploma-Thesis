package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;

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

    @PatchMapping("/me/image")
    public ResponseEntity<?> updateUserImage(@RequestParam("image") MultipartFile image,
                                             Authentication authentication) {
        String username = authentication.getName();

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        if (!image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("File must be an image");
        }

        try {
            String imageUrl = "/images/" + image.getOriginalFilename(); // Пример
            return userService.updateUserImage(username, imageUrl)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading image");
        }
    }
}
