package com.store.shoppingcart.controller;

import com.store.shoppingcart.domain.ShoppingCart;
import com.store.shoppingcart.service.ShopppingCartService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cart")
public class ShoppingCartController extends GenericController<ShoppingCart>{
    public ShoppingCartController(ShopppingCartService service){ super(service); }
}
