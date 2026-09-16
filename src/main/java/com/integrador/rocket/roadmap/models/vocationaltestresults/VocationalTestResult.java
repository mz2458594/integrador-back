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

    @CreatedDate
    private LocalDateTime completedAt;

    @ManyToMany(mappedBy = "vocationalTestResults")
    private List<VocationalOption> vocationalOptions;

    
}
