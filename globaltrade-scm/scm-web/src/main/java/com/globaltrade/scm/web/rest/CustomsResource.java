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
    public CustomsDocumentDTO get(@PathParam("id") Long id) throws CustomsDocumentNotFoundException {
        return customsService.findById(id);
    }

    @POST
    @Path("/documents")
    public Response create(CustomsDocumentDTO input) throws ShipmentNotFoundException, CustomsComplianceException {
        CustomsDocumentDTO created = customsService.create(input);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/deadlines")
    public List<CustomsDocumentDTO> upcomingDeadlines() {
        return customsService.findUpcomingDeadlines(3);
    }
}