package otus.ms.social.controller;

import otus.ms.social.model.dto.UserProfileDto;
import otus.ms.social.model.entity.User;
import otus.ms.social.model.mapper.UserMapper;
import otus.ms.social.repository.UserReadOnlyRepository;
import otus.ms.social.repository.UserRepository;
import otus.ms.social.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "UserController", description = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserRepository userRepository;
    private final UserReadOnlyRepository userReadOnlyRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Изменить профиль")
    public UserProfileDto updateProfile(@RequestBody @Valid UserProfileDto profile) {
        User user = userService.getAuthorizedUserAndCheckUuid(profile.getUuid(), true, false);
        userRepository.save(userMapper.toUser(user, profile));
        return userMapper.toUserProfileDto(user);
    }


    @GetMapping ("/get/{uuid}")
    @Operation(summary = "Посмотреть профиль")
    public UserProfileDto getById(@PathVariable("uuid") UUID userId) {
        return userReadOnlyRepository
                .findByUuid(userId, false)
                .map(userMapper::toUserProfileDto)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + userId));
    }


    @GetMapping ("/search")
    @Operation(summary = "Поиск анкет")
    public List<UserProfileDto> searchByName(@NotBlank @RequestParam("first_name") String firstName,
                                             @NotBlank @RequestParam("last_name") String lastName) {
        List<UserProfileDto> result = userReadOnlyRepository
                .findLikeFirstAndLastNames(firstName.trim(), lastName.trim())
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .map(userMapper::toUserProfileDto)
                .collect(Collectors.toList());

        //log.debug("/user/search found " + result.size() + " records by " + firstName + " and " + lastName);
        return result;
    }
}


