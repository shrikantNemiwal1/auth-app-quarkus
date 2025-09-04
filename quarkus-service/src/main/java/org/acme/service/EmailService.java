package org.acme.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger log = Logger.getLogger(EmailService.class);

    @Inject
    Mailer mailer;

    // Changed to point to Node.js server instead of Quarkus directly
    @ConfigProperty(name = "app.frontend-url", defaultValue = "http://localhost:3000")
    String frontendUrl;

    public void sendVerificationEmail(String email, String token) {
        log.infof("Sending verification email to: %s", email);

        try {
            // Now points to Node.js server which will proxy to Quarkus
            String link = frontendUrl + "/verify?token=" + token;

            mailer.send(
                    Mail.withText(email,
                            "Verify your email",
                            String.format("Hi,\n\nClick this link to verify your email:\n%s\n\nThis link will expire in 24 hours.\n\nThanks!", link))
            );

            log.infof("Verification email sent successfully to: %s", email);
        } catch (Exception e) {
            log.errorf(e, "Failed to send verification email to: %s", email);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
