package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.*;
import com.integrador.rocket.roadmap.models.users.dto.*;
import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.*;
import com.integrador.rocket.roadmap.models.posts.*;
import com.integrador.rocket.roadmap.models.posts.dto.*;
import com.integrador.rocket.roadmap.models.tags.Tag;
import com.integrador.rocket.roadmap.models.tags.dto.TagList;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.*;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.*;
import com.integrador.rocket.roadmap.models.vocationalOption.*;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.*;
import com.integrador.rocket.roadmap.repositories.*;
import com.integrador.rocket.roadmap.services.RoadmapGraphService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Transactional
public class AdminController {
 private final UserRepository users;
 private final RoadmapRepository roadmaps;
 private final PostRepository posts;
 private final TagRepository tags;
 private final VocationalQuestionRepository questions;
 private final VocationalOptionRepository options;
 private final RoadmapLikeRepository likes;
 private final RoadmapGraphService graph;
 private final PasswordEncoder encoder;
 public AdminController(UserRepository users, RoadmapRepository roadmaps, PostRepository posts, TagRepository tags,
   VocationalQuestionRepository questions, VocationalOptionRepository options, RoadmapLikeRepository likes,
   RoadmapGraphService graph, PasswordEncoder encoder) {
  this.users=users; this.roadmaps=roadmaps; this.posts=posts; this.tags=tags; this.questions=questions;
  this.options=options; this.likes=likes; this.graph=graph; this.encoder=encoder;
 }
 public record UserWrite(@NotBlank String name, @NotBlank @Email String email, @NotNull Role role,
   @Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_]).{8,}$", message="Usa 8 caracteres, mayuscula, minuscula, numero y simbolo") String password,
   String university, String biography, @NotNull Boolean active) {}
 public record UserRow(Long id,String name,String email,Role role,String university,String biography,boolean active,
   LocalDateTime createdAt,int postsCount,int roadmapsCount) {}
 private UserRow row(User u) { return new UserRow(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.getUniversity(),u.getBiography(),u.isActive(),u.getCreatedAt(),u.getPosts().size(),u.getRoadmaps().size()); }
 @GetMapping("/users") @Transactional(readOnly=true)
 public Page<UserRow> users(@PageableDefault(size=100,sort="id") Pageable page) {return users.findAll(page).map(this::row);}
 @PostMapping("/users")
 public UserRow createUser(@RequestBody @Valid UserWrite body) {
  if(body.password()==null)throw new ValidationException("La contrasena es obligatoria");
  var user=new User(new UserRegister(body.name(),body.email(),body.role(),body.password()),encoder.encode(body.password()));
  user.actualizar(new UserUpdate(body.name(),body.email(),body.role(),null,body.university(),body.biography()),user.getPassword());
  user.setActiveByAdmin(body.active()); return row(users.saveAndFlush(user));
 }
 @PutMapping("/users/{id}")
 public UserRow updateUser(@PathVariable Long id,@RequestBody @Valid UserWrite body,@AuthenticationPrincipal User actor) {
  var user=users.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
  if(id.equals(actor.getId()) && (!body.active() || body.role()!=Role.ADMINISTRADOR))throw new ValidationException("No puedes desactivar tu cuenta ni quitarte el rol de administrador");
  user.actualizar(new UserUpdate(body.name(),body.email(),body.role(),body.password(),body.university(),body.biography()),body.password()==null?user.getPassword():encoder.encode(body.password()));
  user.setActiveByAdmin(body.active());return row(user);
 }
 @DeleteMapping("/users/{id}")
 public void deleteUser(@PathVariable Long id,@AuthenticationPrincipal User actor) {
  if(id.equals(actor.getId()))throw new ValidationException("No puedes desactivar tu propia cuenta");
  users.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new).eliminar();
 }
 public record RoadmapWrite(@NotBlank String title,@NotBlank String description,@NotBlank String category,
   boolean isPublic,@NotNull Long userId) {}
 public record RoadmapRow(Long id,String title,String description,String category,boolean isPublic,Long userId,
   String authorName,LocalDateTime createdAt,long likesCount,int stepsCount) {}
 private RoadmapRow row(Roadmap r) {return new RoadmapRow(r.getId(),r.getTitle(),r.getDescription(),r.getCategory(),r.isPublic(),r.getUser().getId(),r.getUser().getName(),r.getCreatedAt(),likes.countByRoadmapId(r.getId()),r.getRoadmapSteps().size());}
 @GetMapping("/roadmaps") @Transactional(readOnly=true)
 public Page<RoadmapRow> roadmaps(@PageableDefault(size=100,sort="id") Pageable page) {return roadmaps.findAll(page).map(this::row);}
 @PostMapping("/roadmaps")
 public RoadmapRow createRoadmap(@RequestBody @Valid RoadmapWrite body) {
  var owner=users.findById(body.userId()).orElseThrow(EntityNotFoundException::new);
  return row(roadmaps.saveAndFlush(new Roadmap(new RoadmapRegister(body.title(),body.description(),body.category(),body.isPublic(),CreationSource.MANUAL),owner)));
 }
 @PutMapping("/roadmaps/{id}")
 public RoadmapRow updateRoadmap(@PathVariable Long id,@RequestBody @Valid RoadmapWrite body) {
  var roadmap=roadmaps.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
  if(!roadmap.getUser().getId().equals(body.userId()))throw new ValidationException("El propietario no se puede cambiar");
  roadmap.actualizar(new RoadmapUpdate(body.title(),body.description(),body.category(),body.isPublic()));return row(roadmap);
 }
 @DeleteMapping("/roadmaps/{id}")
 public void deleteRoadmap(@PathVariable Long id) {
  var roadmap=roadmaps.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
  graph.removeRoadmap(id);roadmaps.delete(roadmap);
 }
 public record PostWrite(@NotBlank String title,@NotBlank String content,@NotNull Long userId,List<Long> tagIds) {}
 public record PostRow(Long id,String title,String content,Long userId,String authorName,LocalDateTime createdAt,
   Integer votesCount,Integer views,List<TagList> tags) {}
 private PostRow row(Post p) {return new PostRow(p.getId(),p.getTitle(),p.getContent(),p.getUser().getId(),p.getUser().getName(),p.getCreatedAt(),p.getVotesCount(),p.getViews(),p.getTags().stream().map(TagList::new).toList());}
 private List<Tag> tags(List<Long> ids) {
  var unique=new LinkedHashSet<>(ids==null?List.<Long>of():ids); var result=tags.findAllById(unique);
  if(result.size()!=unique.size())throw new ValidationException("Una categoria no existe"); return result;
 }
 @GetMapping("/posts") @Transactional(readOnly=true)
 public Page<PostRow> posts(@PageableDefault(size=100,sort="id") Pageable page) {return posts.findAll(page).map(this::row);}
 @PostMapping("/posts")
 public PostRow createPost(@RequestBody @Valid PostWrite body) {
  return row(posts.saveAndFlush(new Post(new PostRegister(body.title(),body.content(),body.tagIds()),users.findById(body.userId()).orElseThrow(EntityNotFoundException::new),tags(body.tagIds()))));
 }
 @PutMapping("/posts/{id}")
 public PostRow updatePost(@PathVariable Long id,@RequestBody @Valid PostWrite body) {
  var post=posts.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
  if(!post.getUser().getId().equals(body.userId()))throw new ValidationException("El autor no se puede cambiar");
  post.actualizar(new PostUpdate(body.title(),body.content(),body.tagIds()),tags(body.tagIds()));return row(post);
 }
 @DeleteMapping("/posts/{id}")
 public void deletePost(@PathVariable Long id) {posts.delete(posts.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new));}
 @GetMapping("/questions") @Transactional(readOnly=true)
 public Page<VocationalQuestionList> questions(@PageableDefault(size=100,sort="id") Pageable page) {return questions.findAll(page).map(VocationalQuestionList::new);}
 public record QuestionWrite(@NotBlank @Size(max=255) String text) {}
 public record OptionWrite(@NotBlank @Size(max=255) String text,@NotEmpty Map<@NotBlank String,@NotNull @PositiveOrZero @Max(100) Integer> careerScore) {}
 @PostMapping("/questions")
 public VocationalQuestionList createQuestion(@RequestBody @Valid QuestionWrite body) {return new VocationalQuestionList(questions.saveAndFlush(new VocationalQuestion(new VocationalQuestionRegister(body.text(),null))));}
 @PutMapping("/questions/{id}")
 public VocationalQuestionList updateQuestion(@PathVariable Long id,@RequestBody @Valid QuestionWrite body) {
  var question=questions.findById(id).orElseThrow(EntityNotFoundException::new);question.actualizar(new VocationalQuestionUpdate(body.text()));return new VocationalQuestionList(question);
 }
 @PostMapping("/questions/{id}/options")
 public VocationalOptionDetail createOption(@PathVariable Long id,@RequestBody @Valid OptionWrite body) {
  var question=questions.findById(id).orElseThrow(EntityNotFoundException::new);
  return new VocationalOptionDetail(options.saveAndFlush(new VocationalOption(new VocationalOptionRegister(id,body.text(),body.careerScore()),question)));
 }
 @PutMapping("/options/{id}")
 public VocationalOptionDetail updateOption(@PathVariable Long id,@RequestBody @Valid OptionWrite body) {
  var option=options.findById(id).orElseThrow(EntityNotFoundException::new); option.actualizar(new VocationalOptionUpdate(body.text(),body.careerScore())); return new VocationalOptionDetail(option);
 }
 private void detachAnswers(VocationalOption option) {
  for(var result:List.copyOf(option.getVocationalTestResults()))result.getVocationalOptions().removeIf(item->item.getId().equals(option.getId()));
 }
 @DeleteMapping("/options/{id}")
 public void deleteOption(@PathVariable Long id) {
  var option=options.findById(id).orElseThrow(EntityNotFoundException::new);detachAnswers(option);
  option.getQuestion().getVocationalOptions().removeIf(item->item.getId().equals(id));options.delete(option);
 }
 @DeleteMapping("/questions/{id}")
 public void deleteQuestion(@PathVariable Long id) {
  var question=questions.findById(id).orElseThrow(EntityNotFoundException::new);
  question.getVocationalOptions().forEach(this::detachAnswers);questions.delete(question);
 }
}
