package com.integrador.rocket.roadmap.models.ai;

import com.integrador.rocket.roadmap.models.ai.dto.AiRegister;
import com.integrador.rocket.roadmap.models.ai.dto.AiUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserUpdate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ai_interactions")
public class AiInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AiPurpose purpose;

    @Column(nullable = true)
    private Long relatedRoadmapId;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AiInteraction(@Valid AiRegister aiRegister, User user) {
        this.purpose = aiRegister.purpose();
        if (aiRegister.relatedRoadmapId() != null){
            this.relatedRoadmapId = aiRegister.relatedRoadmapId();
        }
        this.user =user;
    }

    public void actualizar(AiUpdate aiUpdate) {

        if (aiUpdate.purpose() != null) {
            this.purpose = aiUpdate.purpose();
        }

        if (aiUpdate.relatedRoadmapId() != null) {
            this.relatedRoadmapId = aiUpdate.relatedRoadmapId();
        }
    }

}
