package com.learnify.plans.plans.service;

import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.common.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class GetPlanService {
    final private Logger logger = LoggerFactory.getLogger(GetPlanService.class.getName());

    @Autowired
    private PlanRepository planRepository;

    public PlanDTO run(String id) {
        logger.info(format("Search plan with id %s...", id));
        Plan plan = planRepository.findById(id).orElseThrow(() -> {
            logger.info("Plan not found");
            throw new NotFoundException(format("Plano de id %s encontrado", id));
        });

        logger.info("Plan found, return...");
        return new PlanDTO(plan);
    }
}
