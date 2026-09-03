package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.LoginRequest;
import com.globaltrade.scm.common.dto.LoginResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());

    private static final String[] KNOWN_ROLES = {
            "COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REPRESENTATIVE"
    };

    @Context
    private HttpServletRequest httpServletRequest;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            httpServletRequest.login(request.username(), request.password());
        } catch (ServletException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid username or password"))
                    .build();
        }
        httpServletRequest.getSession(true);
        return buildSessionResponse();
    }

    @GET
    @Path("/me")
    public Response me() {
        if (httpServletRequest.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return buildSessionResponse();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        try {
            httpServletRequest.logout();
        } catch (ServletException e) {
            LOGGER.fine(() -> "Logout reported an error, proceeding to invalidate the session anyway: " + e.getMessage());
        }
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.noContent().build();
    }

    private Response buildSessionResponse() {
        String role = resolveRole();
        if (role == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", "Authenticated but no recognized role — check the security-role-mapping configuration"))
                    .build();
        }
        return Response.ok(new LoginResponse(httpServletRequest.getUserPrincipal().getName(), role)).build();
    }

    private String resolveRole() {
        for (String candidate : KNOWN_ROLES) {
            if (httpServletRequest.isUserInRole(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}