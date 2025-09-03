package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.entity.EmailVerificationToken;
import org.acme.entity.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Transactional
    public User registerUser(String email, String password) {
        if (User.find("email", email).firstResult() != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.email = email;
        user.passwordHash = BcryptUtil.bcryptHash(password);
        user.persist();

        // Create verification token
        EmailVerificationToken token = new EmailVerificationToken();
        token.token = UUID.randomUUID().toString();
        token.user = user;
        token.expiresAt = Instant.now().plusSeconds(24*60*60);
        token.persist();

        return user;
    }

    @Transactional
    public boolean verifyEmail(String token) {
        EmailVerificationToken evt = EmailVerificationToken.find("token", token).firstResult();
        if (evt == null || evt.expiresAt.isBefore(Instant.now())) {
            return false;
        }

        evt.user.emailVerified = true;
        evt.user.persist();
        evt.delete();
        return true;
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(User.find("email", email).firstResult());
    }
}
