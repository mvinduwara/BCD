package lk.jiat.web.exception.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lk.jiat.web.dto.ErrorResponse;
import lk.jiat.web.exception.InvalidCredentialsException;

import java.util.Map;

@Provider
public class InvalidCredentialsExceptionMapper
        implements ExceptionMapper<InvalidCredentialsException> {
    @Override
    public Response toResponse(InvalidCredentialsException exception) {

        ErrorResponse body = ErrorResponse.of(
                "unauthorized",
                exception.getMessage(),
                Response.Status.UNAUTHORIZED.getStatusCode()
        );


//        return Response.status(Response.Status.UNAUTHORIZED)
//                .type(MediaType.APPLICATION_JSON)
//                .entity(
//                        Map.of(
//                                "error", "unauthorized",
//                                "message", exception.getMessage()
//                        )
//                ).build();

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}
