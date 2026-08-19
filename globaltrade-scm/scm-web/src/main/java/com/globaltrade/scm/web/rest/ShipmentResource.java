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
    public ShipmentDTO get(@PathParam("id") Long id) throws ShipmentNotFoundException {
        return shipmentService.findById(id);
    }

    @POST
    public Response create(ShipmentDTO input) throws VendorNotFoundException {
        ShipmentDTO created = shipmentService.create(input);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/status")
    public ShipmentDTO updateStatus(@PathParam("id") Long id, Map<String, String> body) throws ShipmentNotFoundException {
        ShipmentStatus status = ShipmentStatus.valueOf(body.get("status"));
        return shipmentService.updateStatus(id, status);
    }

    @PUT
    @Path("/bulk-status")
    public BatchUpdateResult bulkUpdateStatus(Map<String, String> body) {
        Map<Long, ShipmentStatus> updates = new HashMap<>();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            updates.put(Long.parseLong(entry.getKey()), ShipmentStatus.valueOf(entry.getValue()));
        }
        return shipmentBatchService.bulkUpdateStatus(updates);
    }
}