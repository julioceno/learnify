package com.learnify.plans.signatures.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Table(name = "signatures")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "plan_id")
    private String planId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
