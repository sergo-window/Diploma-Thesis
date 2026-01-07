package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads")
@Validated
public class AdController {

    private final ru.skypro.homework.service.AdService adService;

    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = adService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @PostMapping
    public ResponseEntity<Ad> addAd(
            @RequestParam("properties") @Valid CreateOrUpdateAd properties,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        String username = authentication.getName();

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String imageUrl = "/ads/images/" + image.getOriginalFilename(); // Пример
            return adService.createAd(properties, imageUrl, username)
                    .map(ad -> ResponseEntity.status(HttpStatus.CREATED).body(ad))
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable("id") Integer id) {
        return adService.getAdById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAd(@PathVariable("id") Integer id,
                                      Authentication authentication) {
        String username = authentication.getName();
        boolean deleted = adService.deleteAd(id, username);
        return deleted ? ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication) {

        String username = authentication.getName();
        return adService.updateAd(id, createOrUpdateAd, username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        String username = authentication.getName();
        Ads ads = adService.getAdsByAuthor(username);
        return ResponseEntity.ok(ads);
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<byte[]> updateImage(
            @PathVariable("id") Integer id,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        String username = authentication.getName();

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String imageUrl = "/ads/images/" + image.getOriginalFilename(); // Пример
            boolean updated = adService.updateAdImage(id, imageUrl, username);
            return updated ? ResponseEntity.ok(new byte[0]) :
                    ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
