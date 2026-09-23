package com.integrador.rocket.roadmap.models.vocationaltestresults;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionUpdate;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultRegister;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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

    @CreatedDate
    private LocalDateTime completedAt;

    @ManyToMany
    @JoinTable(
            name = "vocational_answer",
            joinColumns = @JoinColumn(name = "result_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<VocationalOption> vocationalOptions = new ArrayList<>();


    public VocationalTestResult(@Valid VocationalTestResultRegister vocationalTestResultRegister, List<VocationalOption> vocationalOptions, User user) {

        this.user = user;
        this.suggestedCareer = vocationalTestResultRegister.suggestedCareer();
        this.vocationalOptions = vocationalOptions;
    }


    public void actualizar(VocationalTestResult vocationalTestResult) {
        if (vocationalTestResult.suggestedCareer != null) {
            this.suggestedCareer = vocationalTestResult.suggestedCareer;
        }
    }
}
