package com.learnify.order.order;

import com.learnify.order.common.Util;
import com.learnify.order.common.dto.ResponseListDTO;
import com.learnify.order.common.dto.UserDTO;
import com.learnify.order.order.dto.CreateOrderDTO;
import com.learnify.order.order.dto.OrderDTO;
import com.learnify.order.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "order")
public class OrderController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        UserDTO user = Util.deserializeUser(request);
        orderService.create(user, createOrderDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResponseListDTO<OrderDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserDTO user = Util.deserializeUser(request);
        ResponseListDTO<OrderDTO> data = orderService.findAll(page, size, user.getId());
        return ResponseEntity.ok(data);
    }
}
