package com.learnify.payment.cancelSignature.services;

import com.learnify.payment.common.dto.MessageQueueDTO;
import com.learnify.payment.common.dto.UserQueueDTO;
import com.learnify.payment.common.service.PublishMessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CancelSignatureService {
    @Value("aws.services.queue.url-return-cancel-signature")
    String returnCancelSignature;

    @Autowired
    private PublishMessageQueueService publishMessageQueueService;

    public void run(MessageQueueDTO<UserQueueDTO> userQueueDTO) {
        // TODO: aplicar lógica de cancelamento no stripe
        publishMessageQueueService.run(returnCancelSignature, userQueueDTO);
    }
}
