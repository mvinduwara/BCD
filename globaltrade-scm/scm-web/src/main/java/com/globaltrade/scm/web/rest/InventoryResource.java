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
    public InventoryItemDTO get(@PathParam("itemId") Long itemId) throws InventoryItemNotFoundException {
        return inventoryService.findById(itemId);
    }

    @PUT
    @Path("/{itemId}/quantity")
    public InventoryItemDTO updateQuantity(@PathParam("itemId") Long itemId, Map<String, Integer> body) throws InventoryItemNotFoundException {
        return inventoryService.updateQuantity(itemId, body.get("quantityOnHand"));
    }

    @GET
    @Path("/low-stock")
    public List<InventoryItemDTO> lowStock() {
        return inventoryService.findLowStock();
    }
}