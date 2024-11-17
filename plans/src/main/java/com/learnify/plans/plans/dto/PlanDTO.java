package com.learnify.plans.plans.dto;

import com.learnify.plans.plans.domain.Permission;
import com.learnify.plans.plans.domain.Plan;
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

    public PlanDTO(Plan plan) {
        id = plan.getId();
        name = plan.getName();
        description = plan.getDescription();
        value = plan.getValue();
        stripeId = plan.getStripeId();
        permissions = plan.getPermissions().stream().map(PermissionDTO::new).toList();
    }
}
