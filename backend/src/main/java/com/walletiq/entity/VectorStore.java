package com.walletiq.entity;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "vector_store")
@Getter
@Setter
@NoArgsConstructor
public class VectorStore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    /**
     * VECTOR(768)
     */
    @Column(columnDefinition = "vector(768)")
    private PGvector embedding;

}
