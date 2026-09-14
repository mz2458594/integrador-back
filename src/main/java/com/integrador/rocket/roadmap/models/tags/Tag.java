package com.integrador.rocket.roadmap.models.tags;

import com.integrador.rocket.roadmap.models.posts.Post;
import jakarta.persistence.*;
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

    private String name;

    @ManyToMany(mappedBy = "tags") //nombre de la variable en la clase post
    private List<Post> posts = new ArrayList<>();

}
