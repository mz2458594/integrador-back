package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.posts.dto.PostDetail;
import com.integrador.rocket.roadmap.models.posts.dto.PostList;
import com.integrador.rocket.roadmap.models.posts.dto.PostRegister;
import com.integrador.rocket.roadmap.models.posts.dto.PostUpdate;
import com.integrador.rocket.roadmap.models.tags.Tag;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<Page<PostList>> listarPosts(@PageableDefault(size = 10) Pageable pageable) {
        var posts = postRepository.findAll(pageable).map(post -> new PostList(post, commentRepository.countByPostId(post.getId())));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PostList>> listarPostsPorUsuarioId(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        var posts = postRepository.findAllByUserId(pageable, user.getId()).map(post -> new PostList(post, commentRepository.countByPostId(post.getId())));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetail> detallePost(@PathVariable Long id) {
        var post = postRepository.getReferenceById(id);
        return ResponseEntity.ok(new PostDetail(post));
    }


    @Transactional
    @PostMapping
    public ResponseEntity<PostDetail> crearPost(@AuthenticationPrincipal User user, @RequestBody @Valid PostRegister postRegister, UriComponentsBuilder uriComponentsBuilder) {

        List<Tag> tags = new ArrayList<>();

        if (postRegister.tagIds() != null) {
            for (Long e : postRegister.tagIds()) {
                var tag = tagRepository.getReferenceById(e);
                tags.add(tag);
            }
        }

        var post = postRepository.save(new Post(postRegister, user, tags));

        var uri = uriComponentsBuilder.path("/post/{id}").buildAndExpand(post.getId()).toUri();

        return ResponseEntity.created(uri).body(new PostDetail(post));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<PostDetail> actualizarPost(@AuthenticationPrincipal User user, @RequestBody PostUpdate postUpdate, @PathVariable Long id) {

        var post = postRepository.getReferenceById(id);

        if (!post.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("No puedes editar un post que no es tuyo");
        }

        List<Tag> tags = null;

        if (postUpdate.tagIds() != null) {
            tags = tagRepository.findAllById(postUpdate.tagIds());
            if (tags.size() != post.getTags().size()){
                throw new EntityNotFoundException("Uno o más tags no existen. Solo se pueden editar la misma cantidad de tags del post");
            }
        }

        post.actualizar(postUpdate, tags);

        return ResponseEntity.ok(new PostDetail(post));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarPost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
