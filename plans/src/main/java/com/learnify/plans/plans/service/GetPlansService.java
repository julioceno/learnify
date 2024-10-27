package com.learnify.plans.plans.service;

import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.plans.dto.PlanDTO;
import com.learnify.plans.plans.dto.ResponseListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class GetPlansService {
    private final Logger logger = LoggerFactory.getLogger(GetPlansService.class.getName());

    @Autowired
    private PlanRepository planRepository;

    // TODO: adicionar filtro por nome
    public ResponseListDTO<PlanDTO> run(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanDTO> plans = getPlans(pageable);

        // TODO: testar se o retorno esta correto
        ResponseListDTO<PlanDTO> responseListDTO = new ResponseListDTO<PlanDTO>(
                plans.getTotalElements(),
                plans.getTotalPages(),
                plans.getNumber(),
                plans.stream().toList()
        );

        return responseListDTO;
    }

    private Page<PlanDTO> getPlans(Pageable pageable) {
        logger.info("Finding plans...");
        Page<PlanDTO> plans = planRepository.findAll(pageable)
                .map(PlanDTO::new);
        logger.info(format("%s plans found", plans.getTotalElements()));
        return plans;
    }
}
