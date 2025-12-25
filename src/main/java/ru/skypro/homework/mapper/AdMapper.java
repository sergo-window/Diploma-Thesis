package ru.skypro.homework.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdMapper {
    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "pk", source = "id")
    @Mapping(target = "image", expression = "java(getImageUrl(entity))")
    Ad toDto(AdEntity entity);

    default String getImageUrl(AdEntity entity) {
        return entity.getImage() != null ? "/ads/image/" + entity.getId() : null;
    }

    @Mapping(target = "pk", source = "id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.username")
    @Mapping(target = "phone", source = "author.phone")
    ExtendedAd toExtendedAdDto(AdEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AdEntity toEntity(CreateOrUpdateAd dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(CreateOrUpdateAd dto, @MappingTarget AdEntity entity);

    default Ads toAdsDto(List<AdEntity> entities) {
        if (entities == null) {
            return null;
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
