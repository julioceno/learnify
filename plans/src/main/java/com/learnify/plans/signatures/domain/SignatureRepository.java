package com.learnify.plans.signatures.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, String> {
    Optional<Signature> findByUserId(String userid);
}
