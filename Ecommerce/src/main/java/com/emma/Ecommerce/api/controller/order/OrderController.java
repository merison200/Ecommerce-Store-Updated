package com.emma.Ecommerce.api.controller.order;

import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.WebOrder;
import com.emma.Ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*This fetches the orders of the particular user, the orders a particular user demanded.You
    must be logged in and put the bearer token authentication token before using this api. How to
    test this api is by inputing the bearer token, https://localhost/8080/order
    */
    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user) {
        return orderService.getOrders(user);
    }
}
