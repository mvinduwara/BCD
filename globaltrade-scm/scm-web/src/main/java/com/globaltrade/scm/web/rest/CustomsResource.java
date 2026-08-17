package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.CustomsDocumentDTO;
import com.globaltrade.scm.exception.CustomsComplianceException;
import com.globaltrade.scm.exception.CustomsDocumentNotFoundException;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.session.customs.CustomsService;

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

@Path("/customs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomsResource {

    @EJB
    private CustomsService customsService;

    @GET
    @Path("/documents")
    public List<CustomsDocumentDTO> list() {
        return customsService.findAll();
    }

    @GET
    @Path("/documents/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            return Response.ok(customsService.findById(id)).build();
        } catch (CustomsDocumentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @POST
    @Path("/documents")
    public Response create(CustomsDocumentDTO input) {
        try {
            return Response.status(Response.Status.CREATED).entity(customsService.create(input)).build();
        } catch (ShipmentNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", e.getMessage())).build();
        } catch (CustomsComplianceException e) {
            return Response.status(422).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @GET
    @Path("/deadlines")
    public List<CustomsDocumentDTO> upcomingDeadlines() {
        return customsService.findUpcomingDeadlines(3);
    }
}