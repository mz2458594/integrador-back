package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.posts.dto.PostList;
import com.integrador.rocket.roadmap.models.tags.Tag;
import com.integrador.rocket.roadmap.models.tags.dto.TagDetail;
import com.integrador.rocket.roadmap.models.tags.dto.TagList;
import com.integrador.rocket.roadmap.models.tags.dto.TagRegister;
import com.integrador.rocket.roadmap.models.tags.dto.TagUpdate;
import com.integrador.rocket.roadmap.repositories.CommentRepository;
import com.integrador.rocket.roadmap.repositories.TagRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/tag")
@SecurityRequirement(name = "bearer-key")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentRepository commentRepository;

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
    public ResponseEntity<TagDetail> detalleTag(@PathVariable Long id) {
        var tag = tagRepository.getReferenceById(id);
        var post = tag.getPosts().stream().map(p -> new PostList(p, commentRepository.countByPostId(p.getId()))).toList();
        return ResponseEntity.ok(new TagDetail(tag, post));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<TagDetail> crearStepResource(@RequestBody @Valid TagRegister tagRegister, UriComponentsBuilder uriComponentsBuilder) {


        var tag = tagRepository.save(new Tag(tagRegister));
        var posts = tag.getPosts().stream().map(p -> new PostList(p, commentRepository.countByPostId(p.getId()))).toList();

        var uri = uriComponentsBuilder.path("/tag/{id}").buildAndExpand(tag.getId()).toUri();

        return ResponseEntity.created(uri).body(new TagDetail(tag, posts));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<TagDetail> actualizarTag(@RequestBody TagUpdate tagUpdate, @PathVariable Long id) {

        var tag = tagRepository.getReferenceById(id);

        tag.actualizar(tagUpdate);

        var posts = tag.getPosts().stream().map(p -> new PostList(p, commentRepository.countByPostId(p.getId()))).toList();

        return ResponseEntity.ok(new TagDetail(tag, posts));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
