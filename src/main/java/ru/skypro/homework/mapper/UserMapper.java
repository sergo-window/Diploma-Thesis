package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", source = "username")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "image", expression = "java(getImageUrl(userEntity))")
    User toUserDto(UserEntity userEntity);

    default String getImageUrl(UserEntity entity) {
        if (entity == null || entity.getImagePath() == null || entity.getImagePath().isEmpty()) {
            return "";
        }
        return "/images/" + entity.getImagePath();
    }

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    UserEntity toEntity(ru.skypro.homework.dto.Register dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateEntityFromDto(UpdateUser dto, @MappingTarget UserEntity entity);
}
