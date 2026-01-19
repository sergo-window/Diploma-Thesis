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
import ru.skypro.homework.dto.*;

import javax.validation.Valid;
import java.io.IOException;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestPart("properties") CreateOrUpdateAd properties,
                                    @RequestPart("image") MultipartFile image) {
        try {
            Ad ad = adService.addAd(properties, image, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(ad);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAdImage(@PathVariable Integer id,
                                                @RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = adService.updateAdImage(id, image);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
