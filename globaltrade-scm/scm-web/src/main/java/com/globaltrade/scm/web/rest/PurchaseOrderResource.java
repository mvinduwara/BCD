package com.globaltrade.scm.web.rest;

import com.globaltrade.scm.common.dto.PlacePurchaseOrderRequest;
import com.globaltrade.scm.common.dto.PurchaseOrderDTO;
import com.globaltrade.scm.exception.AccessDeniedException;
import com.globaltrade.scm.exception.InventoryItemNotFoundException;
import com.globaltrade.scm.exception.PurchaseOrderNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;
import com.globaltrade.scm.session.purchaseorder.PurchaseOrderService;

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

import java.util.List;

@Path("/purchase-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PurchaseOrderResource {

    @EJB
    private PurchaseOrderService purchaseOrderService;

    @GET
    public List<PurchaseOrderDTO> list() {
        return purchaseOrderService.findAll();
    }

    @POST
    public Response place(PlacePurchaseOrderRequest request) throws VendorNotFoundException, InventoryItemNotFoundException {
        PurchaseOrderDTO order = purchaseOrderService.place(request.vendorId(), request.inventoryItemId(), request.quantity());
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @PUT
    @Path("/{id}/confirm")
    public PurchaseOrderDTO confirm(@PathParam("id") Long id) throws PurchaseOrderNotFoundException, AccessDeniedException {
        return purchaseOrderService.confirm(id);
    }

    @PUT
    @Path("/{id}/fulfill")
    public PurchaseOrderDTO fulfill(@PathParam("id") Long id) throws PurchaseOrderNotFoundException {
        return purchaseOrderService.fulfill(id);
    }
}