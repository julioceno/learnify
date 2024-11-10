package com.learnify.plans.signatures.domain;

import com.learnify.plans.plans.domain.Permission;
import com.learnify.plans.plans.domain.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Table(name = "signature_permissions")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignaturePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "signature_id")
    private Plan signature;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
