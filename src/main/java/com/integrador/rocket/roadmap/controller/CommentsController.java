package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.comments.Comment;
import com.integrador.rocket.roadmap.models.comments.dto.CommentDetail;
import com.integrador.rocket.roadmap.models.comments.dto.CommentList;
import com.integrador.rocket.roadmap.models.comments.dto.CommentRegister;
import com.integrador.rocket.roadmap.models.comments.dto.CommentUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.CommentRepository;
import com.integrador.rocket.roadmap.repositories.PostRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@RestController
@RequestMapping("/comment")
@SecurityRequirement(name = "bearer-key")
public class CommentsController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<Page<CommentList>> listarComentarios(@PageableDefault(size = 10) Pageable pageable) {
        var commentLists = commentRepository.findAll(pageable).map(CommentList::new);
        return ResponseEntity.ok(commentLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDetail> detalleComentario(@PathVariable Long id) {
        var comment = commentRepository.getReferenceById(id);
        return ResponseEntity.ok(new CommentDetail(comment));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<CommentDetail> crearComentario(@AuthenticationPrincipal User user, @RequestBody @Valid CommentRegister commentRegister, UriComponentsBuilder uriComponentsBuilder) {

        var post = postRepository.findById(commentRegister.post_id()).orElseThrow(() -> new RuntimeException("No se encontro el Post con este ID"));

        var comment = commentRepository.save(new Comment(commentRegister, user, post));

        var uri = uriComponentsBuilder.path("/comment/{id}").buildAndExpand(comment.getId()).toUri();

        return ResponseEntity.created(uri).body(new CommentDetail(comment));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<CommentDetail> actualizarComentario(@RequestBody CommentUpdate commentUpdate, @PathVariable Long id) {
        var comentario = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el comentario con este ID"));
        comentario.actualizar(commentUpdate);
        return ResponseEntity.ok(new CommentDetail(comentario));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarComentario(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
