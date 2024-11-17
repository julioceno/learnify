package com.learnify.order.order.service;

import com.learnify.order.order.dto.plan.PlanDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Slf4j
@Service
public class GetPlanService {
    @Value("${api.plans.url}")
    private String plansUrl;

    private final RestTemplate restTemplate;

    public GetPlanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PlanDTO run(String planId) {
        log.info("Getting plan with id {}...", planId);
        String url = plansUrl + "/plans";
        PlanDTO planDTO = restTemplate.getForObject(url, PlanDTO.class);
        log.info(format("Plan obtained, %s", planId));

        return planDTO;
    }
}
