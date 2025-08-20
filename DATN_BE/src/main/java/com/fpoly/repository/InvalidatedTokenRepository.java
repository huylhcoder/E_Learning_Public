package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.InvalidatedToken;

import lombok.NonNull;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    boolean existsById(@NonNull String id);
}
