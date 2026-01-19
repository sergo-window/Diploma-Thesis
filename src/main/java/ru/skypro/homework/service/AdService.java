package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final UserService userService;
    private final ImageService imageService;

    public Ads getAllAds() {
        List<AdEntity> ads = adRepository.findAllWithAuthor();
        return adMapper.toAdsDto(ads);
    }

    public Optional<ExtendedAd> getAdById(Integer id) {
        return adRepository.findByIdWithAuthor(id)
                .map(adMapper::toExtendedAdDto);
    }

    public Ads getAdsByAuthor(String username) {
        UserEntity author = userService.getUserEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<AdEntity> ads = adRepository.findAllByAuthor(author);
        return adMapper.toAdsDto(ads);
    }

    @Transactional
    public Ad addAd(CreateOrUpdateAd createOrUpdateAd, MultipartFile image, String username) throws IOException {
        UserEntity author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AdEntity adEntity = adMapper.toEntity(createOrUpdateAd);
        adEntity.setAuthor(author);

        if (image != null && !image.isEmpty()) {
            String imageFilename = imageService.saveImage(image);
            adEntity.setImagePath(imageFilename);
        }

        AdEntity savedEntity = adRepository.save(adEntity);
        return adMapper.toDto(savedEntity);
    }

    @Transactional
    public String updateAdImage(Integer adId, MultipartFile image) throws IOException {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        if (adEntity.getImagePath() != null) {
            try {
                imageService.deleteImage(adEntity.getImagePath());
            } catch (IOException e) {
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        String newImageFilename = imageService.saveImage(image);
        adEntity.setImagePath(newImageFilename);
        adRepository.save(adEntity);

        return imageService.getImageUrl(newImageFilename);
    }

    public ExtendedAd getExtendedAd(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        return adMapper.toExtendedAdDto(adEntity);
    }

    @Transactional
    public Optional<Ad> createAd(CreateOrUpdateAd createAd, String imageUrl, String username) {
        UserEntity author = userService.getUserEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AdEntity adEntity = adMapper.toEntity(createAd);
        adEntity.setAuthor(author);
        adEntity.setImage(imageUrl);

        AdEntity saved = adRepository.save(adEntity);
        return Optional.of(adMapper.toDto(saved));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @adService.isAdAuthor(#id, authentication.name)")
    public Optional<Ad> updateAd(Integer id, CreateOrUpdateAd updateAd, String username) {
        return adRepository.findById(id)
                .filter(ad -> isAdAuthor(id, username))
                .map(ad -> {
                    adMapper.updateEntityFromDto(updateAd, ad);
                    AdEntity saved = adRepository.save(ad);
                    return adMapper.toDto(saved);
                });
    }

    public boolean isAdAuthor(Integer adId, String username) {
        UserEntity user = userService.getUserEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return adRepository.existsByIdAndAuthor(adId, user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @adService.isAdAuthor(#id, authentication.name)")
    public boolean updateAdImage(Integer id, String imageUrl, String username) {
        Optional<AdEntity> adOpt = adRepository.findById(id)
                .filter(ad -> isAdAuthor(id, username));

        if (adOpt.isEmpty()) {
            return false;
        }

        AdEntity ad = adOpt.get();
        ad.setImage(imageUrl);
        adRepository.save(ad);
        return true;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @adService.isAdAuthor(#id, authentication.name)")
    public boolean deleteAd(Integer id, String username) {
        Optional<AdEntity> adOpt = adRepository.findById(id);

        if (adOpt.isEmpty()) {
            return false;
        }

        AdEntity ad = adOpt.get();

        if (!isAdAuthor(id, username) && !userIsAdmin(username)) {
            return false;
        }

        adRepository.delete(ad);
        return true;
    }

    private boolean userIsAdmin(String username) {
        return userService.getUserEntityByUsername(username)
                .map(user -> user.getRole().name().equals("ADMIN"))
                .orElse(false);
    }
}
