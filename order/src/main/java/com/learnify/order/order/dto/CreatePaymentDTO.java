package com.learnify.order.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreatePaymentDTO {
        private String userId;
        private String planId;
        private String cardNumber;
        private String expMonth;
        private String expYear;
        private String cvc;

}
