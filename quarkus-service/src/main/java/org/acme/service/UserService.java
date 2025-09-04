package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.entity.EmailVerificationToken;
import org.acme.entity.User;
import org.acme.exceptions.InvalidTokenException;
import org.acme.exceptions.UserAlreadyExistsException;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private static final Logger log = Logger.getLogger(UserService.class);

    @Transactional
    public EmailVerificationToken registerUser(String email, String password) {
        log.infof("Attempting to register user with email: %s", email);

        if (User.existsByEmail(email)) {
            log.warnf("Registration failed - email already exists: %s", email);
            throw new UserAlreadyExistsException("Email already registered");
        }

        try {
            User user = User.builder()
                    .email(email.toLowerCase().trim())
                    .passwordHash(BcryptUtil.bcryptHash(password))
                    .build();
            user.persist();

            EmailVerificationToken token = EmailVerificationToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .expiresAt(Instant.now().plusSeconds(24 * 60 * 60)) // 24 hours
                    .build();
            token.persist();

            log.infof("User registered successfully: %s", email);
            return token;
        } catch (Exception e) {
            log.errorf(e, "Error registering user: %s", email);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @Transactional
    public void verifyEmail(String tokenString) {
        log.infof("Attempting to verify email with token: %s", tokenString);

        if (tokenString == null || tokenString.trim().isEmpty()) {
            throw new InvalidTokenException("Token is required");
        }

        EmailVerificationToken token = EmailVerificationToken.findByToken(tokenString)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (token.isExpired()) {
            log.warnf("Token expired for user: %s", token.getUser().getEmail());
            throw new InvalidTokenException("Verification token has expired");
        }

        try {
            token.getUser().setEmailVerified(true);
            token.getUser().persist();
            token.delete();
            log.infof("Email verified successfully for user: %s", token.getUser().getEmail());
        } catch (Exception e) {
            log.errorf(e, "Error verifying email for token: %s", tokenString);
            throw new RuntimeException("Failed to verify email", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return User.findByEmail(email.toLowerCase().trim());
    }
}
