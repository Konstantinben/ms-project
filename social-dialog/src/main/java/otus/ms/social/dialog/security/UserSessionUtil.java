package otus.ms.social.dialog.security;

import otus.ms.social.dialog.model.entity.User;
import otus.ms.social.dialog.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionUtil {

    private final AdminJwtTokenProvider adminJwtTokenProvider;

    private final AuthServiceClient authServiceClient;

    public User getAuthorizedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User)auth.getPrincipal()).getUsername();

        return Optional.ofNullable(authServiceClient.getUserByEmail(email, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user by email " + email));
    }
}
