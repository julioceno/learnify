package com.learnify.plans.plans;

import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import com.learnify.plans.plans.service.PlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "plans")
public class PlansController {

    @Autowired
    private PlansService plansService;

    @GetMapping
    public ResponseEntity<ResponseListDTO<PlanDTO>> getPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ResponseListDTO<PlanDTO> data = plansService.getPlans(page, size);
        return ResponseEntity.ok(data);
    }

}
