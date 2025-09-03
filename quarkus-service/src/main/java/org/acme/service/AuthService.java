package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @Inject
    UserService userService;

    public Optional<String> login(String email, String password) {
        Optional<User> maybeUser = userService.findByEmail(email);
        if (maybeUser.isEmpty()) return Optional.empty();

        User user = maybeUser.get();
        if (!BcryptUtil.matches(password, user.passwordHash)) {
            return Optional.empty();
        }

        Set<String> roles = new HashSet<>();
        roles.add("User");

        String token = Jwt.issuer("example-app")
                .subject(user.email)
                .groups(roles)
                .claim("emailVerified", user.emailVerified)
                .sign();

        return Optional.of(token);
    }
}
