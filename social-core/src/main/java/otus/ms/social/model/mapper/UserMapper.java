package otus.ms.social.model.mapper;

import otus.ms.social.model.dto.UserProfileDto;
import otus.ms.social.model.entity.AuthUser;
import otus.ms.social.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel =  "spring")
public interface UserMapper {

    UserProfileDto toUserProfileDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    User toUser(@MappingTarget User user, UserProfileDto profileDto);

    User toUser(AuthUser signupRequestDto);
}
