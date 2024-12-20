package com.learnify.plans.signatures.services;

import com.learnify.plans.common.dto.MessageQueueDTO;
import com.learnify.plans.common.dto.ReturnErrorDTO;
import com.learnify.plans.common.exceptions.ConflictException;
import com.learnify.plans.common.service.PublishMessageQueueService;
import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.learnify.plans.signatures.domain.Signature;
import com.learnify.plans.signatures.domain.SignaturePermission;
import com.learnify.plans.signatures.domain.SignatureRepository;
import com.learnify.plans.signatures.dto.ReturnSignature;
import com.learnify.plans.signatures.dto.SignatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HandleCreateSignatureService {
    @Value("${aws.services.queue.url.return-signature}")
    private String returnSignatureUrl;

    private final SignatureRepository signatureRepository;
    private final PlanRepository planRepository;
    private final PublishMessageQueueService publishMessageQueueService;

    public HandleCreateSignatureService(SignatureRepository signatureRepository, PlanRepository planRepository, PublishMessageQueueService publishMessageQueueService) {
        this.signatureRepository = signatureRepository;
        this.planRepository = planRepository;
        this.publishMessageQueueService = publishMessageQueueService;
    }

    public void run(MessageQueueDTO<SignatureDTO> signatureDTO) {
        try {
            validateIfExists(signatureDTO.data().userId());
            Plan plan = getPlan(signatureDTO.data().planId());
            Signature signature = createSignature(signatureDTO, plan);

            log.info("Return success...");
            ReturnSignature returnSignature = new ReturnSignature(signatureDTO.data().orderId(), signatureDTO.data().userId(), signature.getId());
            MessageQueueDTO<ReturnSignature> message = new MessageQueueDTO<ReturnSignature>(true, returnSignature);
            publishMessageQueueService.run(returnSignatureUrl, message);
        } catch (ConflictException e) {
            log.error("Return error...", e);
            ReturnErrorDTO returnErrorDTO = new ReturnErrorDTO(
                    signatureDTO.data().orderId(),
                    signatureDTO.data().userId(),
                    e.getStatus(),
                    e.getMessage()
            );
            MessageQueueDTO<ReturnErrorDTO> message = new MessageQueueDTO<ReturnErrorDTO>(false, returnErrorDTO);
            publishMessageQueueService.run(returnSignatureUrl, message);
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

    private Signature createSignature(MessageQueueDTO<SignatureDTO> signatureDTO, Plan plan) {
        log.info("Creating signature...");
        Signature signatureToCreate = new Signature();
        BeanUtils.copyProperties(signatureDTO.data(), signatureToCreate);

        List<SignaturePermission> signaturePermissions = generatePermissions(signatureToCreate, plan);
        signatureToCreate.setSignaturePermissions(signaturePermissions);

        Signature signature = signatureRepository.save((signatureToCreate));
        log.info("Signature created");

        return signature;
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
}
