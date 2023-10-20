package otus.ms.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import otus.ms.social.model.entity.User;
import otus.ms.social.repository.UserRepository;
import otus.ms.social.service.UserService;

import java.util.UUID;

@Tag(name = "FriendController", description = "Контроллер списка друзей")
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PutMapping("/set/{user_id}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Добавить в друзья")
    public UUID add(@PathVariable("user_id") UUID friendUuid) {
        User user = userService.getAuthorizedUser(false, false);
        if (user != null) {
            userRepository.insertFriend(user.getId(), friendUuid);
        }
        return friendUuid;
    }

    @DeleteMapping("/delete/{user_id}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Удалить из друзей")
    public UUID delete(@PathVariable("user_id") UUID friendUuid) {
        User user = userService.getAuthorizedUser(false, false);
        if (user != null) {
            userRepository.deleteFriend(user.getId(), friendUuid);
        }
        return friendUuid;
    }
}
