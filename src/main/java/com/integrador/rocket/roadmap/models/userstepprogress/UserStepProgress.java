package com.integrador.rocket.roadmap.models.userstepprogress;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressRegister;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressUpdate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_step_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "step_id"}))
public class UserStepProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @CreatedDate
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private RoadmapStep roadmapStep;

    public UserStepProgress(@Valid UserStepProgressRegister userStepProgressRegister, RoadmapStep roadmapStep, User user) {

        this.status = userStepProgressRegister.status();
        this.user = user;
        this.roadmapStep = roadmapStep;

    }

    public void actualizar(UserStepProgressUpdate userStepProgressUpdate) {
        this.status = userStepProgressUpdate.status();
    }
}
