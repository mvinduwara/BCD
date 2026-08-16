package com.globaltrade.scm.web.exception;

import jakarta.ejb.EJBAccessException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class EjbAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {
    @Override
    public Response toResponse(EJBAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", "You do not have permission to perform this action"))
                .build();
    }
}