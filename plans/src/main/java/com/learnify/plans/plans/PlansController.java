package com.learnify.plans.plans;

import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import com.learnify.plans.plans.service.PlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "plans")
public class PlansController {
    @Autowired
    private PlansService plansService;

    @GetMapping
    public ResponseEntity<ResponseListDTO<PlanDTO>> getPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String description
    ) {
        ResponseListDTO<PlanDTO> data = plansService.getPlans(page, size, name, description);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(
        @PathVariable String id
    ) {
        PlanDTO data = plansService.getPlan(id);
        return ResponseEntity.ok(data);
    }
}
