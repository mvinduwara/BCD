package lk.jiat.web.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import lk.jiat.web.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

@Path("/user") // */*
//@Consumes({MediaType.APPLICATION_JSON})
//@Produces({MediaType.APPLICATION_XML})
public class UserResource {

    @GET
    public String m(@QueryParam("orderBy") List<String> orderBy, @DefaultValue("100") @QueryParam("age") int age) {
        return "OrderBy : " + orderBy.toString() + " Age : " + age;
    }


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@FormParam("name") String name, @FormParam("file") List<EntityPart> parts) throws IOException { //JAX-RS 3.1 EE 10
        System.out.println("Name : " + name);

        for(EntityPart part : parts) {
            System.out.println(part.getName());
            System.out.println(part.getFileName());
            InputStream content = part.getContent();
        }
        return "OK";
    }

}
