package com.learnify.plans.plans.service;

import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.common.FilterSpecification;
import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class GetPlansService {
    private final Logger logger = LoggerFactory.getLogger(GetPlansService.class.getName());

    @Autowired
    private PlanRepository planRepository;

    public ResponseListDTO<PlanDTO> run(int page, int size, String name, String description) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Plan> filters = Specification
                .where(FilterSpecification.<Plan>contains("name", name))
                .and(FilterSpecification.contains("description", description));

        Page<PlanDTO> plans = getPlans(filters, pageable);

        ResponseListDTO<PlanDTO> responseListDTO = new ResponseListDTO<PlanDTO>(
                plans.getTotalElements(),
                plans.getTotalPages(),
                plans.getNumber(),
                plans.stream().toList()
        );

        logger.info("Return list...");
        return responseListDTO;
    }

    private Page<PlanDTO> getPlans(Specification<Plan> filters, Pageable pageable) {
        logger.info("Finding plans...");
        Page<PlanDTO> plans = planRepository.findAll(filters, pageable)
                .map(PlanDTO::new);
        logger.info(format("%s plans found", plans.getTotalElements()));
        return plans;
    }
}
