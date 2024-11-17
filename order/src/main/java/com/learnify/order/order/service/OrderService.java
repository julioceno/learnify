package com.learnify.order.order.service;

import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.order.dto.CreateOrderDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
    private final CreateOrderService createOrderService;

    public void create(UserDTO user, CreateOrderDTO createOrderDTO) {
        createOrderService.run(user, createOrderDTO);
    }
}
