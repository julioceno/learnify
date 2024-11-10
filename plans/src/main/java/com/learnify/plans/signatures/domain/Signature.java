package com.learnify.plans.signatures.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "signatures")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "plan_id")
    private String planId;

    @OneToMany(mappedBy = "signature", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SignaturePermission> signaturePermissions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
