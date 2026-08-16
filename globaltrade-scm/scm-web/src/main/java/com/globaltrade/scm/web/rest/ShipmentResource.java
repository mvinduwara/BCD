package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.BatchUpdateResult;
import com.globaltrade.scm.common.dto.ShipmentDTO;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.session.shipment.ShipmentBatchService;
import com.globaltrade.scm.session.shipment.ShipmentService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/shipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShipmentResource {

    @EJB
    private ShipmentService shipmentService;

    @EJB
    private ShipmentBatchService shipmentBatchService;

    @GET
    public List<ShipmentDTO> list() {
        return shipmentService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            return Response.ok(shipmentService.findById(id)).build();
        } catch (ShipmentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @POST
    public Response create(ShipmentDTO input) {
        try {
            return Response.status(Response.Status.CREATED).entity(shipmentService.create(input)).build();
        } catch (VendorNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, Map<String, String> body) {
        try {
            ShipmentStatus status = ShipmentStatus.valueOf(body.get("status"));
            return Response.ok(shipmentService.updateStatus(id, status)).build();
        } catch (ShipmentNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", "Unknown status: " + body.get("status"))).build();
        }
    }

    @PUT
    @Path("/bulk-status")
    public Response bulkUpdateStatus(Map<String, String> body) {
        Map<Long, ShipmentStatus> updates = new HashMap<>();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            updates.put(Long.parseLong(entry.getKey()), ShipmentStatus.valueOf(entry.getValue()));
        }
        BatchUpdateResult result = shipmentBatchService.bulkUpdateStatus(updates);
        return Response.ok(result).build();
    }
}