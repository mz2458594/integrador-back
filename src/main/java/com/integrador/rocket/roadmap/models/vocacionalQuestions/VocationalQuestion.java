package com.integrador.rocket.roadmap.models.vocacionalQuestions;

import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import jakarta.persistence.*;
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
    private int orderIndex;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<VocationalOption> vocacionalOptions;




}
