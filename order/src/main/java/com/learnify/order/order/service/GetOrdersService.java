package com.learnify.order.order.service;

import com.learnify.order.common.dto.ResponseListDTO;
import com.learnify.order.order.domain.OrderRepository;
import com.learnify.order.order.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class GetOrdersService {
    private OrderRepository orderRepository;

    public ResponseListDTO<OrderDTO> run(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = getOrders(pageable, userId);

        ResponseListDTO<OrderDTO> responseListDTO = new ResponseListDTO<OrderDTO>(
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.getNumber(),
                orders.stream().toList()
        );

        log.info("Return list...");
        return responseListDTO;
    }

    private Page<OrderDTO> getOrders(Pageable pageable, String userId) {
        log.info("Finding orders");
        Page<OrderDTO> orders = orderRepository.findByUserId(userId, pageable).map(OrderDTO::new);
        log.info("{} orders found", orders.getTotalElements());
        return orders;
    }
}
