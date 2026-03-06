package com.walletiq.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@NoArgsConstructor
public class Admin extends BaseEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 150, unique = true)
    private String email;
}
