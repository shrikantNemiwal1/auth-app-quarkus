package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.dto.LoginResponse;
import org.acme.entity.User;
import org.acme.exceptions.AuthenticationException;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class);

    @Inject
    UserService userService;

    public LoginResponse authenticate(String email, String password) {
        log.infof("Authentication attempt for email: %s", email);

        Optional<User> maybeUser = userService.findByEmail(email);
        if (maybeUser.isEmpty()) {
            throw new AuthenticationException("Invalid email or password");
        }

        User user = maybeUser.get();
        if (!BcryptUtil.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        String message = user.isEmailVerified()
                ? "Your email is validated. You can access the portal"
                : "You need to validate your email to access the portal";

        return LoginResponse.builder()
                .token("") // Empty token since Node.js handles sessions
                .userId(user.getId().toString())
                .emailVerified(user.isEmailVerified())
                .message(message)
                .build();
    }
}

