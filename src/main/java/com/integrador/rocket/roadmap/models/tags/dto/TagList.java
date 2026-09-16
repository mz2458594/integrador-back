package com.integrador.rocket.roadmap.models.tags.dto;

import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.posts.dto.PostDetail;
import com.integrador.rocket.roadmap.models.tags.Tag;

import java.util.List;

public record TagList(
        Long id,
        String name
) {
    public TagList(Tag tag) {
        this(
                tag.getId(),
                tag.getName()
        );

    }

}
