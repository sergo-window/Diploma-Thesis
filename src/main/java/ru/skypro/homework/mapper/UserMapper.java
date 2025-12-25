package ru.skypro.homework.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "email", source = "username")
    @Mapping(target = "id", source = "id")
    User toUserDto(UserEntity entity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    UserEntity toEntity(User dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateEntityFromDto(UpdateUser dto, @MappingTarget UserEntity entity);
}
