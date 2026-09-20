package com.integrador.rocket.roadmap.models.tags;

import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.tags.dto.TagRegister;
import com.integrador.rocket.roadmap.models.tags.dto.TagUpdate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags") //nombre de la variable en la clase post
    private List<Post> posts = new ArrayList<>();

    public Tag(@Valid TagRegister tagRegister) {

        this.name = tagRegister.name();

    }

    public void actualizar(TagUpdate tagUpdate) {
        if (tagUpdate.name() != null){
            this.name = name;
        }
    }

    //EL TAG SE CREA PRIMERO, LUEGO SE LE ASOCIA A UN POST EN EL CONTROLADOR DE POST

}
