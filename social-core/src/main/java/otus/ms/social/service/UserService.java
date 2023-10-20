package otus.ms.social.service;

import otus.ms.social.model.entity.AuthUser;
import otus.ms.social.model.entity.User;
import otus.ms.social.model.exception.AccessForbiddenException;
import otus.ms.social.model.mapper.UserMapper;
import otus.ms.social.repository.UserRepository;
import otus.ms.social.security.UserSessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserSessionUtil userSessionUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getAuthorizedUserAndCheckUuid(UUID userUuid, boolean createIfNotExists, boolean insertId) {
        User user = getAuthorizedUser(createIfNotExists, insertId);
        if (!userUuid.equals(user.getUuid())) {
            log.error("Cannot perform the action for user " + user.getUuid());
            throw new AccessForbiddenException("Cannot perform the action for user " + user.getUuid());
        }
        return user;
    }

    public User getAuthorizedUser(boolean createIfNotExists, boolean insertId) {
        AuthUser authUser = null;
        try {
            authUser = userSessionUtil.getAuthorizedUser();
        } catch (Exception e) {
            log.error("User " + authUser.getUuid() + " is not authorized.");
            throw new AuthenticationCredentialsNotFoundException("User not authorized.", e);
        }

        User user = null;
        final AuthUser authenticatedUser = authUser;
        if (createIfNotExists) {
            user = userRepository
                    .findByUuid(authUser.getUuid())
                    .orElseGet(() -> {
                        User emptyUser = userMapper.toUser(authenticatedUser);
                        emptyUser.setId(insertId ? authenticatedUser.getId() : null);
                        return emptyUser;
                    });
        } else {
            user = userRepository
                    .findByUuid(authUser.getUuid())
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Cannod find user by " + authenticatedUser.getUuid()));
        }
        return user;
    }
}
