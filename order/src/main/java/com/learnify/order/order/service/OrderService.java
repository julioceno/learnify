package com.learnify.order.order.service;

import com.learnify.order.common.dto.ResponseListDTO;
import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.order.dto.CreateOrderDTO;
import com.learnify.order.order.dto.OrderDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
    private final CreateOrderService createOrderService;
    private final GetOrdersService getOrdersService;

    public void create(UserDTO user, CreateOrderDTO createOrderDTO) {
        createOrderService.run(user, createOrderDTO);
    }

    public ResponseListDTO<OrderDTO> findAll(int page, int size, String userId) {
        return getOrdersService.run(page, size, userId);
    }
}
