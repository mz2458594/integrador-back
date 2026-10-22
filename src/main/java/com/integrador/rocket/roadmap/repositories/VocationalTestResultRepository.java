package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VocationalTestResultRepository extends JpaRepository<VocationalTestResult, Long> {
    org.springframework.data.domain.Page<VocationalTestResult> findAllByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
    Optional<VocationalTestResult> findByUserId(Long id);
}
