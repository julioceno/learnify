package com.learnify.plans.plans.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface PlanRepository extends JpaRepository<Plan, String>, JpaSpecificationExecutor<Plan> {
}
