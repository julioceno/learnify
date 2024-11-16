package com.learnify.plans.plans.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlanRepository extends JpaRepository<Plan, String>, JpaSpecificationExecutor<Plan> {

    Optional<Plan> findByName(String name);
}
