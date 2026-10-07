package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.posts.dto.PostList;
import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.tags.Tag;
import com.integrador.rocket.roadmap.models.tags.dto.TagDetail;
import com.integrador.rocket.roadmap.models.tags.dto.TagList;
import com.integrador.rocket.roadmap.models.tags.dto.TagRegister;
import com.integrador.rocket.roadmap.models.tags.dto.TagUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.CommentRepository;
import com.integrador.rocket.roadmap.repositories.PostVoteRepository;
import com.integrador.rocket.roadmap.repositories.TagRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    private List<PostList> mapPosts(List<Post> posts, User user) {
        return posts.stream().map(post -> new PostList(
                post,
                commentRepository.countByPostId(post.getId()),
                postVoteRepository.existsByPostIdAndUserId(post.getId(), user.getId())
        )).toList();
    }

    @GetMapping
    public ResponseEntity<Page<TagList>> listarTags(@PageableDefault(size = 10) Pageable pageable) {
        var tagLists = tagRepository.findAll(pageable).map(TagList::new);
        return ResponseEntity.ok(tagLists);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Page<TagList>> listarTagsPorPostId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var tags = tagRepository.findAllByPostsId(pageable, id).map(TagList::new);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDetail> detalleTag(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var tag = tagRepository.getReferenceById(id);
        var post = mapPosts(tag.getPosts(), user);
        return ResponseEntity.ok(new TagDetail(tag, post));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<TagDetail> crearStepResource(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TagRegister tagRegister,
            UriComponentsBuilder uriComponentsBuilder
    ) {


        var tag = tagRepository.save(new Tag(tagRegister));
        var posts = mapPosts(tag.getPosts(), user);

        var uri = uriComponentsBuilder.path("/tag/{id}").buildAndExpand(tag.getId()).toUri();

        return ResponseEntity.created(uri).body(new TagDetail(tag, posts));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<TagDetail> actualizarTag(
            @AuthenticationPrincipal User user,
            @RequestBody TagUpdate tagUpdate,
            @PathVariable Long id
    ) {

        var tag = tagRepository.getReferenceById(id);

        tag.actualizar(tagUpdate);

        var posts = mapPosts(tag.getPosts(), user);

        return ResponseEntity.ok(new TagDetail(tag, posts));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
