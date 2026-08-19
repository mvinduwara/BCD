package com.globaltrade.scm.web.exception;

import com.globaltrade.scm.exception.AccessDeniedException;
import com.globaltrade.scm.exception.ScmComplianceException;
import com.globaltrade.scm.exception.ScmException;
import com.globaltrade.scm.exception.ScmNotFoundException;
import com.globaltrade.scm.exception.ScmValidationException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ScmExceptionMapper implements ExceptionMapper<ScmException> {

    @Override
    public Response toResponse(ScmException exception) {
        return Response.status(statusCodeFor(exception))
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", exception.getMessage()))
                .build();
    }

    private int statusCodeFor(ScmException exception) {
        if (exception instanceof ScmNotFoundException) {
            return Response.Status.NOT_FOUND.getStatusCode();
        }
        if (exception instanceof ScmValidationException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        if (exception instanceof ScmComplianceException) {
            return 422;
        }
        if (exception instanceof AccessDeniedException) {
            return Response.Status.FORBIDDEN.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}