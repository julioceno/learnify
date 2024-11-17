package com.learnify.gateway.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String customerId;
}
