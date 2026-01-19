package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.CommentEntity;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "authorImage", expression = "java(getAuthorImageUrl(commentEntity))")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "pk", source = "id")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtMillis(commentEntity))")
    Comment toDto(CommentEntity commentEntity);

    default String getAuthorImageUrl(CommentEntity entity) {
        if (entity == null || entity.getAuthor() == null ||
                entity.getAuthor().getImagePath() == null ||
                entity.getAuthor().getImagePath().isEmpty()) {
            return "";
        }
        return "/images/" + entity.getAuthor().getImagePath();
    }

    default Long getCreatedAtMillis(CommentEntity entity) {
        if (entity == null || entity.getCreatedAt() == null) {
            return 0L;
        }

        return entity.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
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
            Comments comments = new Comments();
            comments.setCount(0);
            return comments;
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
