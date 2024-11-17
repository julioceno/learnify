package com.learnify.order.order.dto.payment;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    private String cardNumber;
    private String expMonth;
    private String expYear;
    private String cvc;
}
