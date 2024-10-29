package com.learnify.plans.plans.dto;

import com.learnify.plans.plans.domain.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionDTO {
    private String name;
    private String description;

    public PermissionDTO(Permission permission) {
        name = permission.getName();
        description = permission.getDescription();
    }
}
