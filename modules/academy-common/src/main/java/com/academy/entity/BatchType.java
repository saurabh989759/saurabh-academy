package com.academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batch_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
