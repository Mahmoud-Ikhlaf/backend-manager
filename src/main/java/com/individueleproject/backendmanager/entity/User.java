package com.individueleproject.backendmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 20)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 70)
    private String password;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;
}
