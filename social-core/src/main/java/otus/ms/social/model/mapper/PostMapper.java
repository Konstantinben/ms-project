package otus.ms.social.model.mapper;

import otus.ms.social.model.dto.PostDto;
import otus.ms.social.model.dto.PostFriendsDto;
import otus.ms.social.model.entity.Post;
import org.mapstruct.*;

@Mapper(componentModel =  "spring")
public interface PostMapper {

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    Post toPost(PostDto postDto);

    PostDto toPostDto(Post post);

    PostFriendsDto toPostFriendsDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdDate", ignore = true)
    Post toPost(@MappingTarget Post post, PostDto postDto);
}
