package com.learnify.order.order.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlanDTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal value;
    private String stripeId;
    private List<PermissionDTO> permissions;
}
