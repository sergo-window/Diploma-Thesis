package ru.skypro.homework.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.CommentEntity;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "authorImage", expression = "java(getAuthorImageUrl(entity))")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "pk", source = "id")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtMillis(entity))")
    Comment toDto(CommentEntity entity);

    default String getAuthorImageUrl(CommentEntity entity) {
        return entity.getAuthor() != null && entity.getAuthor().getImage() != null ?
                "/users/image/" + entity.getAuthor().getId() : null;
    }

    default Long getCreatedAtMillis(CommentEntity entity) {
        return entity.getCreatedAt() != null ?
                entity.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CommentEntity toEntity(CreateOrUpdateComment dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(CreateOrUpdateComment dto, @MappingTarget CommentEntity entity);

    default Comments toCommentsDto(List<CommentEntity> entities) {
        if (entities == null) {
            return null;
        }

        Comments comments = new Comments();
        comments.setCount(entities.size());
        comments.setResults(
                entities.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
        return comments;
    }
}
