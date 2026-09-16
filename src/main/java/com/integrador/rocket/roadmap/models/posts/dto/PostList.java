package com.integrador.rocket.roadmap.models.posts.dto;

import com.integrador.rocket.roadmap.models.comments.dto.CommentDetail;
import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.tags.dto.TagDetail;
import com.integrador.rocket.roadmap.models.tags.dto.TagList;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;

import java.time.LocalDateTime;
import java.util.List;

public record PostList(
        Long id,
        String title,
        String content,
        Integer views,
        LocalDateTime createdAt,
        UserDetail user,
        List<TagList> tags,

        //VARIABLE APARTE CUANDO SE CREE EL SERVICIO
        //EL NÚMERO TOTAL DE COMENTARIOS
        Long commentsCount
) {
    public PostList(Post post, Long commentsCount) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                post.getCreatedAt(),
                new UserDetail(post.getUser()),
                post.getTags().stream().map(TagList::new).toList(),
                commentsCount
        );
    }
}
