package com.integrador.rocket.roadmap.models.vocationalOption;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionRegister;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionRegisterForQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionUpdate;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "vocacional_option")
@EntityListeners(AuditingEntityListener.class)
public class VocationalOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private VocationalQuestion question;

    private String text;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Integer> careerScore = new HashMap<>();

    @ManyToMany(mappedBy = "vocationalOptions")
    private List<VocationalTestResult> vocationalTestResults = new ArrayList<>();


    public VocationalOption(VocationalOptionRegister v, VocationalQuestion vocationalQuestion) {
        this.text = v.text();
        this.careerScore = v.careerScore();
        this.question = vocationalQuestion;
    }

    public VocationalOption(VocationalOptionRegisterForQuestion v, VocationalQuestion vocationalQuestion) {
        this.text = v.text();
        this.careerScore = v.careerScore();
        this.question = vocationalQuestion;
    }

    public void actualizar(VocationalOptionUpdate vocationalOptionUpdate) {
        if (vocationalOptionUpdate.text() != null) {
            this.text = vocationalOptionUpdate.text();
        }

        if (vocationalOptionUpdate.careerScore() != null) {
            this.careerScore = vocationalOptionUpdate.careerScore();
        }
    }
}
