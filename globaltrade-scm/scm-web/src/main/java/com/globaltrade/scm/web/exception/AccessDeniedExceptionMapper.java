package com.globaltrade.scm.web.exception;

import com.globaltrade.scm.exception.AccessDeniedException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {
    @Override
    public Response toResponse(AccessDeniedException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", exception.getMessage()))
                .build();
    }
}