package com.academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "batches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String name;

    @Column(name = "current_instructor", nullable = false)
    private String currentInstructor;

    @Column(name = "start_month", nullable = false)
    private LocalDate startMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BatchType batchType;

    @ManyToMany
    @JoinTable(
        name = "batches_classes",
        joinColumns = @JoinColumn(name = "batch_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<ClassEntity> classes = new HashSet<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Student> students = new HashSet<>();
}
