package otus.ms.social.security;

import otus.ms.social.client.AuthServiceClient;
import otus.ms.social.model.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsServiceImpol")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthServiceClient authServiceClient;

    private final AdminJwtTokenProvider adminJwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("Username cannot be empty");
        }

        AuthUser user = Optional.ofNullable(authServiceClient.getUserByEmail(username, adminJwtTokenProvider.getAdminHeadersMap()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found by name "+ username));
        return SecurityUser.fromUser(user);
    }
}
