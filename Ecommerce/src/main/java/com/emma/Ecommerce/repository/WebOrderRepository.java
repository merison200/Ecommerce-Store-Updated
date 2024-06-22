package com.emma.Ecommerce.repository;

import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.WebOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
