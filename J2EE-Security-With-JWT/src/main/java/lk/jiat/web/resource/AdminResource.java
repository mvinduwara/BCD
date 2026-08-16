package lk.jiat.web.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/admin")
@RolesAllowed("ADMIN")
public class AdminResource {

    @GET
    @Path("/profile")
    public Response profile() {
        return Response.ok().entity("Admin Profile").build();
    }
}
