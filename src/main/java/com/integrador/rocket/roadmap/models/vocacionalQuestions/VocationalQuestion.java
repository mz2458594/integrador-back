package com.integrador.rocket.roadmap.models.vocacionalQuestions;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionRegister;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionUpdate;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vocational_questions")
public class VocationalQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
//    private Integer orderIndex = 0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<VocationalOption> vocationalOptions;


    public VocationalQuestion(@Valid VocationalQuestionRegister vocationalQuestionRegister) {
        this.text = vocationalQuestionRegister.text();
//        this.orderIndex = vocationalQuestionRegister.order();
    }

    public void actualizar(VocationalQuestionUpdate vocationalQuestionUpdate) {

        if (vocationalQuestionUpdate.text() != null) {
            this.text = vocationalQuestionUpdate.text();
        }

//        if (vocationalQuestionUpdate.order() != null) {
//            this.orderIndex = vocationalQuestionUpdate.order();
//        }

    }
}
