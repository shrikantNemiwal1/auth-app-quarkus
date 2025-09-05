package org.acme.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ApiResponse;
import org.acme.dto.SignupRequest;
import org.acme.entity.EmailVerificationToken;
import org.acme.exceptions.InvalidTokenException;
import org.acme.exceptions.UserAlreadyExistsException;
import org.acme.service.EmailService;
import org.acme.service.UserService;
import org.jboss.logging.Logger;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger log = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    EmailService emailService;

    @POST
    public Response signup(@Valid SignupRequest request) {
        try {
            EmailVerificationToken token = userService.registerUser(request.getEmail(), request.getPassword());
            emailService.sendVerificationEmail(request.getEmail(), token.getToken());

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.builder()
                            .success(true)
                            .message("User created. Please check your email for verification.")
                            .build())
                    .build();
        } catch (UserAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build())
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Error during signup");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message("Failed to create user")
                            .build())
                    .build();
        }
    }

    @GET
    @Path("/verify")
    public Response verify(@QueryParam("token") String token) {
        try {
            userService.verifyEmail(token);
            return Response.ok(ApiResponse.builder()
                            .success(true)
                            .message("Email verified successfully.")
                            .build())
                    .build();
        } catch (InvalidTokenException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build())
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Error during email verification");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message("Failed to verify email")
                            .build())
                    .build();
        }
    }
}
