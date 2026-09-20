package com.integrador.rocket.roadmap.models.tags.dto;

import com.integrador.rocket.roadmap.models.posts.dto.PostList;
import com.integrador.rocket.roadmap.models.tags.Tag;

import java.util.List;

public record TagDetail(
        Long id,
        String name,
        List<PostList> posts
) {
    public TagDetail(Tag tag, List<PostList> postLists) {
        this(
                tag.getId(),
                tag.getName(),
                postLists
        );
    }
}
