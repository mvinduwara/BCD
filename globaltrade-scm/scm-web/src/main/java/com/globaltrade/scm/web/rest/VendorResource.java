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
    public Response get(@PathParam("id") Long id) {
        try {
            return Response.ok(vendorService.findById(id)).build();
        } catch (VendorNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @POST
    public Response create(VendorDTO input) {
        try {
            return Response.status(Response.Status.CREATED).entity(vendorService.create(input)).build();
        } catch (VendorValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}/performance")
    public Response performance(@PathParam("id") Long id) {
        try {
            VendorDTO vendor = vendorService.findById(id);
            return Response.ok(Map.of("vendorId", id, "score", vendor.performanceScore())).build();
        } catch (VendorNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }
}