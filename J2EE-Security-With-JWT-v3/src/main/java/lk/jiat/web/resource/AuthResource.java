package lk.jiat.web.resource;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.web.entity.RefreshToken;
import lk.jiat.web.exception.InvalidCredentialsException;
import lk.jiat.web.model.LoginRequest;
import lk.jiat.web.service.LoginService;
import lk.jiat.web.service.RefreshTokenService;
import lk.jiat.web.util.JwtUtil;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private LoginService loginService;

    @Inject
    private RefreshTokenService refreshTokenService;


    public record LoginRequest(String username, String password) {
    } // JDK 16 above

    @Path("/login")
    @POST
    @Transactional(
            rollbackOn = {Exception.class},
            dontRollbackOn = {InvalidCredentialsException.class}
    )
    public Response login(LoginRequest request) {
        if (request == null || request.username == null || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Missing username and password")).build();
        }

        UsernamePasswordCredential credential = new UsernamePasswordCredential(request.username(), request.password());

        CredentialValidationResult result = identityStoreHandler.validate(credential);

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = JwtUtil.generateToken(result.getCallerPrincipal().getName(), result.getCallerGroups());

            RefreshToken refreshToken = refreshTokenService.create(result.getCallerPrincipal().getName());

            return Response.status(Response.Status.OK).entity(Map.of("accessToken", token, "refreshToken", refreshToken.getToken(), "username", result.getCallerPrincipal().getName(), "roles", result.getCallerGroups())).build();
        }else {
            throw new InvalidCredentialsException("Invalid username or password");
        }

       // return Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("error", "Invalid username or password")).build();

    }

    public record RefreshRequest(String refreshToken) {
    }

    @POST
    @Path("/refresh")
    public Response refresh(RefreshRequest request) {
        if (request == null || request.refreshToken() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Missing refresh token")).build();
        }

        Optional<RefreshToken> tokenOptional = refreshTokenService.findValid(request.refreshToken());
        if (tokenOptional.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(
                    Map.of(
                            "error", "Invalid or expired refresh token")
            ).build();
        }

        RefreshToken oldToken = tokenOptional.get();
        String username = oldToken.getUsername();

        refreshTokenService.deleteToken(oldToken.getToken());
        RefreshToken refreshToken = refreshTokenService.create(username);


        Set<String> roles = loginService.getRoles(username);

        String token = JwtUtil.generateToken(username, roles);

        return Response.status(Response.Status.OK)
                .entity(
                        Map.of(
                                "accessToken", token,
                                "refreshToken", refreshToken.getToken(),
                                "username", username,
                                "roles", roles))
                .build();

    }

    //Optional
    @POST
    @Path("/logout")
    public Response logout(RefreshRequest request) {
        if (request != null && request.refreshToken() != null) {
            refreshTokenService.deleteToken(request.refreshToken());
        }
        return Response.status(Response.Status.OK)
                .entity(Map.of(
                        "message", "Logged Out")
                ).build();
    }
}
