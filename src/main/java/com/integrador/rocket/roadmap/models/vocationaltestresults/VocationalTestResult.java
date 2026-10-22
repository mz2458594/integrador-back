package com.integrador.rocket.roadmap.models.vocationaltestresults;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vocational_test_result")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VocationalTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String suggestedCareer;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<com.integrador.rocket.roadmap.models.vocationaltestresults.dto.CareerScore> careerRanking = new ArrayList<>();

    @CreatedDate
    private LocalDateTime completedAt;

    @ManyToMany
    @JoinTable(
            name = "vocational_answer",
            joinColumns = @JoinColumn(name = "result_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<VocationalOption> vocationalOptions = new ArrayList<>();


    public VocationalTestResult(User user, com.integrador.rocket.roadmap.services.VocationalScoringService.Evaluation evaluation) {
        this.user = user;
        this.suggestedCareer = evaluation.suggestedCareer();
        this.careerRanking = evaluation.ranking();
        this.vocationalOptions = evaluation.answers();
        this.completedAt = LocalDateTime.now();
    }

}
