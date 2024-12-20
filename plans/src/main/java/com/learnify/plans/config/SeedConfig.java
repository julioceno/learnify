package com.learnify.plans.config;

import com.learnify.plans.plans.domain.Permission;
import com.learnify.plans.plans.domain.PermissionRepository;
import com.learnify.plans.plans.domain.Plan;
import com.learnify.plans.plans.domain.PlanRepository;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;
import com.stripe.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("seed")
@EnableJpaAuditing
@AllArgsConstructor
public class SeedConfig implements CommandLineRunner {
    private PlanRepository planRepository;
    private PermissionRepository permissionRepository;
    private StripeClient stripeClient;


    @Override
    public void run(String... args) throws Exception {
        createPlan(
                "Plano Básico",
                "Acesso limitado aos cursos introdutórios",
                new BigDecimal("29.99"),
                new PermissionToCreate[]{
                        new PermissionToCreate("ACCESS_COURSES_BASIC", "Acesso aos cursos básicos"),
                        new PermissionToCreate("NO_DOWNLOAD", "Sem permissão para downloads"),
                        new PermissionToCreate("LIMITED_SUPPORT", "Suporte limitado")
                }
        );

        createPlan(
                "Plano Intermediário",
                "Acesso aos cursos intermediários e suporte prioritário",
                new BigDecimal("59.99"),
                new PermissionToCreate[]{
                        new PermissionToCreate("ACCESS_COURSES_INTERMEDIATE", "Acesso aos cursos intermediários"),
                        new PermissionToCreate("DOWNLOAD_ALLOWED", "Permissão para downloads"),
                        new PermissionToCreate("PRIORITY_SUPPORT", "Suporte prioritário"),
                        new PermissionToCreate("ACCESS_CERTIFICATES", "Acesso aos certificados")
                }
        );

        createPlan(
                "Plano Avançado",
                "Acesso completo a todos os cursos e recursos premium",
                new BigDecimal("99.99"),
                new PermissionToCreate[]{
                        new PermissionToCreate("ACCESS_ALL_COURSES", "Acesso completo a todos os cursos"),
                        new PermissionToCreate("DOWNLOAD_ALLOWED", "Permissão para downloads"),
                        new PermissionToCreate("PREMIUM_SUPPORT", "Suporte premium"),
                        new PermissionToCreate("ACCESS_CERTIFICATES", "Acesso aos certificados"),
                        new PermissionToCreate("ACCESS_EXCLUSIVE_CONTENT", "Acesso a conteúdos exclusivos"),
                        new PermissionToCreate("ACCESS_LIVE_CLASSES", "Acesso a aulas ao vivo")
                }
        );
    }

    private void createPlan(
            String name,
            String description,
            BigDecimal value,
            PermissionToCreate[] permissions
    ) throws StripeException {
        if (planRepository.findByName(name).isPresent()) {
            return;
        }

        ProductCreateParams.DefaultPriceData priceData = ProductCreateParams.DefaultPriceData.builder()
                .setUnitAmount(value.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency("brl")
                .build();

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(name)
                .setDescription(name)
                .setDefaultPriceData(priceData)
                .build();

        Product stripeProductCreate = stripeClient.products().create(params);

        Plan planToCreate = new Plan(
                null,
                name,
                description,
                value,
                stripeProductCreate.getId(),
                null,
                null,
                null
        );
        Plan planCreated = planRepository.save(planToCreate);

        List<Permission> permissionsCreates = Arrays.stream(permissions).map(permissionToCreate -> new Permission(
                null,
                permissionToCreate.getName(),
                permissionToCreate.getDescription(),
                planCreated,
                null,
                null
        )).toList();
        permissionRepository.saveAll(permissionsCreates);
    }

}

@AllArgsConstructor
@Getter
class PermissionToCreate {
    private String name;
    private String description;
}
