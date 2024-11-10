package com.learnify.plans.signatures.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRepository extends JpaRepository<Signature, String> {
}
