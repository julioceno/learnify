package com.learnify.plans.plans.service;

import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PlansService {
    final private GetPlansService getPlansService;
    final private GetPlanService getPlanService;

    public ResponseListDTO<PlanDTO> getPlans(int page, int size) {
        return this.getPlansService.run(page, size);
    }

    public PlanDTO getPlan(String id) {
        return getPlanService.run(id);
    }
}
