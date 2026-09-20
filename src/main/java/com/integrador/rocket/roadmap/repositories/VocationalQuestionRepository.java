package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocationalQuestionRepository extends JpaRepository<VocationalQuestion, Long> {
}
