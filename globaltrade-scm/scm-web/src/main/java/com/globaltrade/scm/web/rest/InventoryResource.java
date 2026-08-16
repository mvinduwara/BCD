package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.InventoryItemDTO;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;
import com.globaltrade.scm.session.inventory.InventoryService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @EJB
    private InventoryService inventoryService;

    @GET
    public List<InventoryItemDTO> list() {
        return inventoryService.findAll();
    }

    @GET
    @Path("/{itemId}")
    public Response get(@PathParam("itemId") Long itemId) {
        try {
            return Response.ok(inventoryService.findById(itemId)).build();
        } catch (InventoryItemNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{itemId}/quantity")
    public Response updateQuantity(@PathParam("itemId") Long itemId, Map<String, Integer> body) {
        try {
            return Response.ok(inventoryService.updateQuantity(itemId, body.get("quantityOnHand"))).build();
        } catch (InventoryItemNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", e.getMessage())).build();
        }
    }

    @GET
    @Path("/low-stock")
    public List<InventoryItemDTO> lowStock() {
        return inventoryService.findLowStock();
    }
}