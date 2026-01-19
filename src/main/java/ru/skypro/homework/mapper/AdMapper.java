package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdMapper {

    @Mapping(target = "author", expression = "java(adEntity.getAuthor().getId())")
    @Mapping(target = "pk", source = "id")
    @Mapping(target = "image", expression = "java(getImageUrl(adEntity))")
    Ad toDto(AdEntity adEntity);

    default String getImageUrl(AdEntity entity) {
        if (entity == null || entity.getImagePath() == null || entity.getImagePath().isEmpty()) {
            return "";
        }
        return "/images/" + entity.getImagePath();
    }

    @Mapping(target = "pk", source = "id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.username")
    @Mapping(target = "phone", source = "author.phone")
    @Mapping(target = "image", expression = "java(getImageUrl(adEntity))")
    ExtendedAd toExtendedAdDto(AdEntity adEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    AdEntity toEntity(CreateOrUpdateAd dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    void updateEntityFromDto(CreateOrUpdateAd dto, @MappingTarget AdEntity entity);

    default Ads toAdsDto(List<AdEntity> entities) {
        if (entities == null) {
            Ads ads = new Ads();
            ads.setCount(0);
            return ads;
        }

        Ads ads = new Ads();
        ads.setCount(entities.size());
        ads.setResults(
                entities.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
        return ads;
    }
}
