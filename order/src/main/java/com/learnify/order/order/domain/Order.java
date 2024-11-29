package com.learnify.order.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
public class Order {
    @Id
    private String id;
    private String planId;
    private String subscriptionId;
    private String signatureId;
    private String messageError;
    private StatusOrder status;
}
