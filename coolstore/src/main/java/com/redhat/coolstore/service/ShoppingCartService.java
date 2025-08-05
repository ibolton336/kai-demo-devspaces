package com.redhat.coolstore.service;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.redhat.coolstore.model.Product;
import com.redhat.coolstore.model.ShoppingCart;
import com.redhat.coolstore.model.ShoppingCartItem;

@SessionScoped
public class ShoppingCartService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    Logger log;

    @Inject
    ProductService productServices;

    @Inject
    PromoService ps;

    @Inject
    ShoppingCartOrderProcessor shoppingCartOrderProcessor;

    private ShoppingCart cart = new ShoppingCart();
    
    private Client client;
    private WebTarget target;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        // Assuming the service is deployed on the same host. Adjust the URL as needed.
        target = client.target("http://localhost:8080/api/shipping");
    }

    public ShoppingCartService() {
    }

    public ShoppingCart getShoppingCart(String cartId) {
        return cart;
    }

    public ShoppingCart checkOutShoppingCart(String cartId) {
        ShoppingCart cart = this.getShoppingCart(cartId);
      
        log.info("Sending order: ");
        shoppingCartOrderProcessor.process(cart);
   
        cart.resetShoppingCartItemList();
        priceShoppingCart(cart);
        return cart;
    }

    public void priceShoppingCart(ShoppingCart sc) {
        if (sc != null) {
            initShoppingCartForPricing(sc);

            if (sc.getShoppingCartItemList() != null && sc.getShoppingCartItemList().size() > 0) {
                ps.applyCartItemPromotions(sc);

                for (ShoppingCartItem sci : sc.getShoppingCartItemList()) {
                    sc.setCartItemPromoSavings(
                            sc.getCartItemPromoSavings() + sci.getPromoSavings() * sci.getQuantity());
                    sc.setCartItemTotal(sc.getCartItemTotal() + sci.getPrice() * sci.getQuantity());
                }

                sc.setShippingTotal(calculateShipping(sc));

                if (sc.getCartItemTotal() >= 25) {
                    sc.setShippingTotal(sc.getShippingTotal() + calculateShippingInsurance(sc));
                }
            }

            ps.applyShippingPromotions(sc);
            sc.setCartTotal(sc.getCartItemTotal() + sc.getShippingTotal());
        }
    }

    private void initShoppingCartForPricing(ShoppingCart sc) {
        sc.setCartItemTotal(0);
        sc.setCartItemPromoSavings(0);
        sc.setShippingTotal(0);
        sc.setShippingPromoSavings(0);
        sc.setCartTotal(0);

        for (ShoppingCartItem sci : sc.getShoppingCartItemList()) {
            Product p = getProduct(sci.getProduct().getItemId());
            //if product exist
            if (p != null) {
                sci.setProduct(p);
                sci.setPrice(p.getPrice());
            }
            sci.setPromoSavings(0);
        }
    }

    public Product getProduct(String itemId) {
        return productServices.getProductByItemId(itemId);
    }

    private double calculateShipping(ShoppingCart cart) {
        return target.path("/calculate")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(cart, MediaType.APPLICATION_JSON), Double.class);
    }

    private double calculateShippingInsurance(ShoppingCart cart) {
        return target.path("/insurance")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(cart, MediaType.APPLICATION_JSON), Double.class);
    }
}