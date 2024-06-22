package com.emma.Ecommerce.service;

import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.WebOrder;
import com.emma.Ecommerce.repository.WebOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private WebOrderRepository webOrderRepository;

    public List<WebOrder> getOrders(LocalUser user) {
        return webOrderRepository.findByUser(user);
    }
}
