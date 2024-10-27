package com.learnify.plans.plans.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Table(name = "permissions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}
