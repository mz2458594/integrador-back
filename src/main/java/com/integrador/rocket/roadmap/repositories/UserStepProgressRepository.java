package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStepProgressRepository extends JpaRepository<UserStepProgress, Long> {
    Page<UserStepProgress> findAllByRoadmapStepId(Pageable pageable, Long id);

    Page<UserStepProgress> findAllByUserId(Pageable pageable, Long id);
}
