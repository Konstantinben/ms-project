package otus.ms.social.mapper;

import otus.ms.social.model.dto.PostDto;
import otus.ms.social.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel =  "spring")
public interface PostMapper {
    PostDto toPostDto(Post post);
}
