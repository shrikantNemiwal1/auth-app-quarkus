package org.acme.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ApiResponse;
import org.acme.dto.LoginRequest;
import org.acme.dto.LoginResponse;
import org.acme.exceptions.AuthenticationException;
import org.acme.service.AuthService;
import org.jboss.logging.Logger;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger log = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        try {
            // Method name changed from 'login' to 'authenticate'
            LoginResponse result = authService.authenticate(request.getEmail(), request.getPassword());
            return Response.ok(result).build();
        } catch (AuthenticationException e) {
            log.warnf("Authentication failed: %s", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build())
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Unexpected error during authentication");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred")
                            .build())
                    .build();
        }
    }

    // Optional: Add endpoint for Node.js to validate tokens
    @POST
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.builder()
                                .success(false)
                                .message("Invalid authorization header")
                                .build())
                        .build();
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.validateInternalToken(token);

            return Response.ok(ApiResponse.builder()
                            .success(isValid)
                            .message(isValid ? "Token valid" : "Token invalid")
                            .build())
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Error validating token");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .success(false)
                            .message("Token validation failed")
                            .build())
                    .build();
        }
    }
}

