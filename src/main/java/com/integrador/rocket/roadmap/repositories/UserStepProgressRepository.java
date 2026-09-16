package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStepProgressRepository extends JpaRepository<UserStepProgress, Long> {
}
