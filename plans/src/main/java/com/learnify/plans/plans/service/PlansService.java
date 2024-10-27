package com.learnify.plans.plans.service;

import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlansService {
    @Autowired
    private GetPlansService getPlansService;

    public ResponseListDTO<PlanDTO> getPlans(int page, int size) {
        return this.getPlansService.run(page, size);
    }

}
