package com.learnify.order.order.dto;

import com.learnify.order.order.domain.Order;
import com.learnify.order.order.domain.StatusOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
public class OrderDTO {
    private String id;
    private String planId;
    private String subscriptionId;
    private String signatureId;
    private String messageError;
    private StatusOrder status;

    public OrderDTO(Order order) {
        id = order.getId();
        planId = order.getPlanId();
        subscriptionId = order.getSubscriptionId();
        signatureId = order.getSignatureId();
        messageError = order.getMessageError();
        status = order.getStatus();
    }
}
