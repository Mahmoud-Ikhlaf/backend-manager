package com.individueleproject.backendmanager.repository;

import com.individueleproject.backendmanager.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findRefreshTokenEntityByToken(String token);
    void deleteByUserId(Long id);
}
