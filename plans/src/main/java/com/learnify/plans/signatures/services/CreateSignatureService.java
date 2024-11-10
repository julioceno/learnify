package com.learnify.plans.signatures.services;

import com.learnify.plans.common.dto.MessageQueueDTO;
import com.learnify.plans.common.exceptions.ConflictException;
import com.learnify.plans.common.service.PublishMessageQueueService;
import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.signatures.domain.Signature;
import com.learnify.plans.signatures.domain.SignaturePermission;
import com.learnify.plans.signatures.domain.SignatureRepository;
import com.learnify.plans.signatures.dto.SignatureDTO;
import com.learnify.plans.signatures.dto.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CreateSignatureService {
    @Value("${aws.services.queue.return-signature}")
    private String returnSignature;

    private final SignatureRepository signatureRepository;
    private final PlanRepository planRepository;
    private final PublishMessageQueueService publishMessageQueueService;

    public CreateSignatureService(SignatureRepository signatureRepository, PlanRepository planRepository, PublishMessageQueueService publishMessageQueueService) {
        this.signatureRepository = signatureRepository;
        this.planRepository = planRepository;
        this.publishMessageQueueService = publishMessageQueueService;
    }

    public void run(SignatureDTO signatureDTO) {
        try {
            validateIfExists(signatureDTO.userId());
            Plan plan = getPlan(signatureDTO.planId());
            createSignature(signatureDTO, plan);

            sendMessage(true, signatureDTO.userId());
        } catch (RuntimeException e) {
            sendMessage(false, signatureDTO.userId());
        }
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

    private Plan getPlan(String planId) {
        log.info("Finding plan by id {} ...", planId);
        Plan plan = planRepository.findById(planId).orElse(null);
        if (plan == null) {
            log.error("Plan not found");
        }

        log.info("Plan exists");
        return plan;
    }

    private void createSignature(SignatureDTO signatureDTO, Plan plan) {
        log.info("Creating signature...");
        Signature signatureToCreate = new Signature();
        BeanUtils.copyProperties(signatureDTO, signatureToCreate);

        List<SignaturePermission> signaturePermissions = generatePermissions(signatureToCreate, plan);
        signatureToCreate.setSignaturePermissions(signaturePermissions);

        signatureRepository.save((signatureToCreate));
        log.info("Signature created");
    }

    private List<SignaturePermission> generatePermissions(Signature signature, Plan plan) {
        log.info("Generating permissions...");
        List<SignaturePermission> signaturesPermission = plan.getPermissions().stream().map(permission -> {
            SignaturePermission signaturePermission = new SignaturePermission();

            signaturePermission.setName(permission.getName());
            signaturePermission.setSignature(signature);
            signaturePermission.setPermission(permission);

            return signaturePermission;
        }).toList();

        log.info("{} permissions generated", signaturesPermission.size());
        return signaturesPermission;
    }

    private void sendMessage(Boolean ok, String userId) {
        MessageQueueDTO<UserId> message = new MessageQueueDTO<UserId>(ok, new UserId(userId));
        publishMessageQueueService.run(returnSignature, message);
    }
}