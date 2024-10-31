package com.learnify.plans.plans.service;

import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
public class GetPlanService {
    @Autowired
    private PlanRepository planRepository;

    public PlanDTO run(String id) {
        log.info(format("Search plan with id %s...", id));
        Plan plan = planRepository.findById(id).orElseThrow(() -> {
            log.info("Plan not found");
            throw new NotFoundException(format("Plano de id %s encontrado", id));
        });

        log.info("Plan found, return...");
        return new PlanDTO(plan);
    }
}
