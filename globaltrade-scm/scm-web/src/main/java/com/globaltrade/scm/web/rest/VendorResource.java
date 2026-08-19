package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.VendorDTO;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.exception.VendorValidationException;
import com.globaltrade.scm.session.vendor.VendorService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/vendors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VendorResource {

    @EJB
    private VendorService vendorService;

    @GET
    public List<VendorDTO> list() {
        return vendorService.findAll();
    }

    @GET
    @Path("/{id}")
    public VendorDTO get(@PathParam("id") Long id) throws VendorNotFoundException {
        return vendorService.findById(id);
    }

    @POST
    public Response create(VendorDTO input) throws VendorValidationException {
        VendorDTO created = vendorService.create(input);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}/performance")
    public Map<String, Object> performance(@PathParam("id") Long id) throws VendorNotFoundException {
        VendorDTO vendor = vendorService.findById(id);
        return Map.of("vendorId", id, "score", vendor.performanceScore());
    }
}