package com.redhat.coolstore.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.coolstore.model.ShoppingCart;
import com.redhat.coolstore.service.ShippingService;

@Path("/shipping")
@Stateless
public class ShippingEndpoint {

    @Inject
    ShippingService shippingService;

    @POST
    @Path("/calculate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public double calculateShipping(ShoppingCart cart) {
        return shippingService.calculateShipping(cart);
    }

    @POST
    @Path("/insurance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public double calculateShippingInsurance(ShoppingCart cart) {
        return shippingService.calculateShippingInsurance(cart);
    }
}