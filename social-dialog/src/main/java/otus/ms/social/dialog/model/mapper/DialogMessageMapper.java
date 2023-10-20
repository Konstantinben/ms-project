package otus.ms.social.dialog.model.mapper;

import otus.ms.social.dialog.model.dto.DialogMessageDto;
import otus.ms.social.dialog.model.entity.DialogMessage;
import otus.ms.social.dialog.model.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel =  "spring")
public interface DialogMessageMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "fromUserUuid", source = "fromUser.uuid"),
            @Mapping(target = "fromFirstName", source = "fromUser.firstName"),
            @Mapping(target = "fromLastName", source = "fromUser.lastName"),
            @Mapping(target = "toUserUuid", source = "toUser.uuid"),
            @Mapping(target = "toFirstName", source = "toUser.firstName"),
            @Mapping(target = "toUserLastName", source = "toUser.lastName"),
            @Mapping(target = "uuid", source = "message.uuid")
    })
    DialogMessageDto toDto(DialogMessage message, User fromUser, User toUser);

    default List<DialogMessageDto> toDtoList(List<DialogMessage> messages, User fromUser, User toUser) {
        return messages.stream().map(message -> this.toDto(message, fromUser, toUser)).collect(Collectors.toList());
    }
}
