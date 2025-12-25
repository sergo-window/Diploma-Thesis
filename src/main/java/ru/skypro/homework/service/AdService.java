package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserService userService;

    public Ads getAllAds() {
        List<AdEntity> ads = adRepository.findAllWithAuthor();
        return adMapper.toAdsDto(ads);
    }

    public Optional<ExtendedAd> getAdById(Integer id) {
        return adRepository.findByIdWithAuthor(id)
                .map(adMapper::toExtendedAdDto);
    }

    public Ads getAdsByAuthor(UserEntity author) {
        List<AdEntity> ads = adRepository.findAllByAuthor(author);
        return adMapper.toAdsDto(ads);
    }

    @Transactional
    public Optional<Ad> createAd(CreateOrUpdateAd createAd, String imageUrl, UserEntity author) {
        AdEntity adEntity = adMapper.toEntity(createAd);
        adEntity.setAuthor(author);
        adEntity.setImage(imageUrl);

        AdEntity saved = adRepository.save(adEntity);
        return Optional.of(adMapper.toDto(saved));
    }

    @Transactional
    public Optional<Ad> updateAd(Integer id, CreateOrUpdateAd updateAd, UserEntity user) {
        return adRepository.findById(id)
                .filter(ad -> ad.getAuthor().getId().equals(user.getId()))
                .map(ad -> {
                    adMapper.updateEntityFromDto(updateAd, ad);
                    AdEntity saved = adRepository.save(ad);
                    return adMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean updateAdImage(Integer id, String imageUrl, UserEntity user) {
        Optional<AdEntity> adOpt = adRepository.findById(id)
                .filter(ad -> ad.getAuthor().getId().equals(user.getId()));

        if (adOpt.isEmpty()) {
            return false;
        }

        AdEntity ad = adOpt.get();
        ad.setImage(imageUrl);
        adRepository.save(ad);
        return true;
    }

    @Transactional
    public boolean deleteAd(Integer id, UserEntity user) {
        Optional<AdEntity> adOpt = adRepository.findById(id);

        if (adOpt.isEmpty()) {
            return false;
        }

        AdEntity ad = adOpt.get();

        if (!ad.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().equals(ru.skypro.homework.model.Role.ADMIN)) {
            return false;
        }

        adRepository.delete(ad);
        return true;
    }

    public boolean isAdAuthor(Integer adId, UserEntity user) {
        return adRepository.existsByIdAndAuthor(adId, user);
    }
}
