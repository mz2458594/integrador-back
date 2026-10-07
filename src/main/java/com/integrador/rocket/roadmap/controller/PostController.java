package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.posts.PostView;
import com.integrador.rocket.roadmap.models.posts.PostVote;
import com.integrador.rocket.roadmap.models.posts.dto.PostDetail;
import com.integrador.rocket.roadmap.models.posts.dto.PostEngagement;
import com.integrador.rocket.roadmap.models.posts.dto.PostList;
import com.integrador.rocket.roadmap.models.posts.dto.PostRegister;
import com.integrador.rocket.roadmap.models.posts.dto.PostUpdate;
import com.integrador.rocket.roadmap.models.posts.dto.PostViewCount;
import com.integrador.rocket.roadmap.models.tags.Tag;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Autowired
    private PostViewRepository postViewRepository;

    @GetMapping
    public ResponseEntity<Page<PostList>> listarPosts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        var posts = postRepository.findAll(pageable).map(post -> new PostList(
                post,
                commentRepository.countByPostId(post.getId()),
                postVoteRepository.existsByPostIdAndUserId(post.getId(), user.getId())
        ));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PostList>> listarPostsPorUsuarioId(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        var posts = postRepository.findAllByUserId(pageable, user.getId()).map(post -> new PostList(
                post,
                commentRepository.countByPostId(post.getId()),
                postVoteRepository.existsByPostIdAndUserId(post.getId(), user.getId())
        ));
        return ResponseEntity.ok(posts);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<PostDetail> detallePost(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var post = postRepository.getReferenceById(id);
        return ResponseEntity.ok(new PostDetail(
                post,
                postVoteRepository.existsByPostIdAndUserId(post.getId(), user.getId())
        ));
    }

    @Transactional
    @PostMapping("/{id}/view")
    public ResponseEntity<PostViewCount> registrarVisualizacion(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        var post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));

        if (!postViewRepository.existsByPostIdAndUserId(id, user.getId())) {
            postViewRepository.save(new PostView(post, user));
            post.incrementViews();
        }

        return ResponseEntity.ok(new PostViewCount(post.getViews()));
    }

    @Transactional
    @PostMapping("/{id}/vote")
    public ResponseEntity<PostEngagement> votarPublicacion(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        var post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));

        if (!postVoteRepository.existsByPostIdAndUserId(id, user.getId())) {
            postVoteRepository.save(new PostVote(post, user));
            post.incrementVotesCount();
        }

        return ResponseEntity.ok(new PostEngagement(post.getVotesCount(), true));
    }

    @Transactional
    @DeleteMapping("/{id}/vote")
    public ResponseEntity<PostEngagement> quitarVoto(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        var post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));

        if (postVoteRepository.existsByPostIdAndUserId(id, user.getId())) {
            postVoteRepository.deleteByPostIdAndUserId(id, user.getId());
            post.decrementVotesCount();
        }

        return ResponseEntity.ok(new PostEngagement(post.getVotesCount(), false));
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
            Set<Long> tagIds = new LinkedHashSet<>(postUpdate.tagIds());
            tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new EntityNotFoundException("Una o más categorías no existen");
            }
        }

        post.actualizar(postUpdate, tags);

        return ResponseEntity.ok(new PostDetail(
                post,
                postVoteRepository.existsByPostIdAndUserId(post.getId(), user.getId())
        ));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPost(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var post = postRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes eliminar una publicación que no es tuya");
        }

        postRepository.delete(post);
        return ResponseEntity.noContent().build();
    }


}
