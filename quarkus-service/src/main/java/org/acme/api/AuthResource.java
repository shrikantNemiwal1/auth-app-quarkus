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
}
