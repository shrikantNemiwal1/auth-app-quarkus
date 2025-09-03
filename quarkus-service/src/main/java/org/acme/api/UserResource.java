package org.acme.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.SignupRequest;
import org.acme.dto.VerifyRequest;
import org.acme.service.UserService;

import java.util.Map;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Response signup(@Valid SignupRequest req) {
        userService.registerUser(req.email(), req.password());
        return Response.status(Response.Status.CREATED).entity(Map.of(
                "message", "User created. Please check your email for verification."
        )).build();
    }

    @POST
    @Path("/verify")
    public Response verify(@Valid VerifyRequest req) {
        userService.verifyEmail(req.token());
        return Response.ok(Map.of("message", "Email verified successfully.")).build();
    }
}
