package com.integrador.rocket.roadmap.models.tags.dto;

import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.posts.dto.PostDetail;
import com.integrador.rocket.roadmap.models.tags.Tag;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public record TagDetail(
        Long id,
        String name,
        List<PostDetail> posts
) {
    public TagDetail(Tag tag) {
        this(
                tag.getId(),
                tag.getName(),
                tag.getPosts().stream().map(PostDetail::new).toList()
        );
    }
}
