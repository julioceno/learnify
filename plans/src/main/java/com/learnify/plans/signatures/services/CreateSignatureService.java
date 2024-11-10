package com.learnify.plans.signatures.services;

import com.learnify.plans.common.exceptions.ConflictException;
import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.signatures.domain.Signature;
import com.learnify.plans.signatures.domain.SignatureRepository;
import com.learnify.plans.signatures.dto.SignatureDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CreateSignatureService {
    private final SignatureRepository signatureRepository;
    private final PlanRepository planRepository;

    public void run(SignatureDTO signatureDTO) {
        validateIfExists(signatureDTO.userId());
        verifyIfPlanExists(signatureDTO.planId());
        createSignature(signatureDTO);
    }

    private void validateIfExists(String userId) {
        log.info("Finding signatures by userId {}", userId);
        Optional<Signature> signature = signatureRepository.findByUserId(userId);

        if (signature.isPresent()) {
            log.error("This user has already subscribed to a plan");
            throw new ConflictException("Já existe um plano para esse usuario");
        }
        log.info("Signature exists");
    }

    private void verifyIfPlanExists(String planId) {
        log.info("Finding plan by id {} ...", planId);
        Optional<Plan> plan = planRepository.findById(planId);
        if (plan.isEmpty()) {
            log.error("Plan not found");
        }

        log.info("Plan exists");
    }

    private void createSignature(SignatureDTO signatureDTO) {
        log.info("Creating signature...");
        Signature signatureToCreate = new Signature();
        BeanUtils.copyProperties(signatureDTO, signatureToCreate);
        signatureRepository.save((signatureToCreate));
        log.info("Signature created");
    }
}
