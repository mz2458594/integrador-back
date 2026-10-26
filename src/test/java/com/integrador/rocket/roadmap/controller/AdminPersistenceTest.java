package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.*;
import com.integrador.rocket.roadmap.models.users.dto.*;
import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.*;
import com.integrador.rocket.roadmap.models.posts.*;
import com.integrador.rocket.roadmap.models.posts.dto.*;
import com.integrador.rocket.roadmap.models.vocationaltestresults.*;
import com.integrador.rocket.roadmap.repositories.*;
import com.integrador.rocket.roadmap.services.*;
import jakarta.persistence.EntityManager;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql=false,properties={"spring.test.database.replace=NONE", "spring.datasource.username=sa", "spring.datasource.password=", "logging.level.root=WARN", "debug=false", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.datasource.url=jdbc:h2:mem:admin_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import({AdminController.class,RoadmapGraphService.class,AdminPersistenceTest.Security.class})
class AdminPersistenceTest {
 @TestConfiguration @EnableMethodSecurity static class Security { @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();} }
 @Autowired AdminController admin;
 @Autowired UserRepository users;
 @Autowired RoadmapRepository roadmaps;
 @Autowired PostRepository posts;
 @Autowired VocationalOptionRepository options;
 @Autowired VocationalQuestionRepository questions;
 @Autowired VocationalTestResultRepository results;
 @Autowired PasswordEncoder encoder;
 @Autowired EntityManager em;
 User actor;
 User user(String name,Role role) {return users.saveAndFlush(new User(new UserRegister(name,name+"@example.test",role,"Strong1!x"),encoder.encode("Strong1!x")));}
 void authenticate(User user){SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));}
 @BeforeEach void setup(){actor=user("Admin",Role.ADMINISTRADOR);authenticate(actor);}
 @AfterEach void cleanup(){SecurityContextHolder.clearContext();}
 @Test void studentCannotReadOrMutateAnyAdminResource(){
  var student=user("Student",Role.ESTUDIANTE);authenticate(student);
  assertThrows(AccessDeniedException.class,()->admin.users(PageRequest.of(0,10)));
  assertThrows(AccessDeniedException.class,()->admin.roadmaps(PageRequest.of(0,10)));
  assertThrows(AccessDeniedException.class,()->admin.posts(PageRequest.of(0,10)));
  assertThrows(AccessDeniedException.class,()->admin.questions(PageRequest.of(0,10)));
  assertThrows(AccessDeniedException.class,()->admin.deleteUser(actor.getId(),student));
  assertThrows(AccessDeniedException.class,()->admin.createQuestion(new AdminController.QuestionWrite("Denied")));
 }
 @Test void userCrudHashesPasswordsPreservesThemOnEditAndCanReactivate(){
  var created=admin.createUser(new AdminController.UserWrite("New","new@example.test",Role.ESTUDIANTE,"Strong1!x","UNI","Bio",true));
  var stored=users.findById(created.id()).orElseThrow();var hash=stored.getPassword();assertTrue(encoder.matches("Strong1!x",hash));
  admin.updateUser(created.id(),new AdminController.UserWrite("Changed","new@example.test",Role.ADMINISTRADOR,null,"UNI","Bio",true),actor);
  assertEquals(hash,stored.getPassword());assertEquals(Role.ADMINISTRADOR,stored.getRole());
  admin.deleteUser(created.id(),actor);assertFalse(stored.isEnabled());
  admin.updateUser(created.id(),new AdminController.UserWrite("Changed","new@example.test",Role.ESTUDIANTE,null,"UNI","Bio",true),actor);assertTrue(stored.isEnabled());
 }
 @Test void adminCanUpdateAndDeleteRoadmapsAndPostsOwnedByAnotherUser(){
  var owner=user("Owner",Role.ESTUDIANTE);
  var roadmap=admin.createRoadmap(new AdminController.RoadmapWrite("Plan","Description","Software",false,owner.getId()));
  var changed=admin.updateRoadmap(roadmap.id(),new AdminController.RoadmapWrite("Edited","Description","Science",true,owner.getId()));
  assertEquals("Science",changed.category());assertTrue(changed.isPublic());
  var post=admin.createPost(new AdminController.PostWrite("Title","Content",owner.getId(),List.of()));
  assertEquals("Edited",admin.updatePost(post.id(),new AdminController.PostWrite("Edited","Content",owner.getId(),List.of())).title());
  admin.deletePost(post.id());admin.deleteRoadmap(roadmap.id());em.flush();
  assertFalse(posts.existsById(post.id()));assertFalse(roadmaps.existsById(roadmap.id()));
 }
 @Test void questionAndOptionCrudDetachesAnswersWithoutDeletingHistoricalResults(){
  var question=admin.createQuestion(new AdminController.QuestionWrite("Question"));
  var option=admin.createOption(question.id(),new AdminController.OptionWrite("Option",Map.of("Software",4)));
  var second=admin.createOption(question.id(),new AdminController.OptionWrite("Other",Map.of("Software",0)));
  em.flush();em.clear();
  var result=new VocationalTestResult(actor,new VocationalScoringService.Evaluation("Software",List.of(),new ArrayList<>(List.of(options.findById(option.id()).orElseThrow()))));
  var resultId=results.saveAndFlush(result).getId();em.clear();
  assertEquals("Updated",admin.updateQuestion(question.id(),new AdminController.QuestionWrite("Updated")).text());
  assertEquals(5,admin.updateOption(second.id(),new AdminController.OptionWrite("Changed",Map.of("Software",5))).careerScore().get("Software"));
  admin.deleteOption(option.id());em.flush();em.clear();
  assertTrue(results.existsById(resultId));assertTrue(results.findById(resultId).orElseThrow().getVocationalOptions().isEmpty());
  admin.deleteQuestion(question.id());em.flush();assertFalse(questions.existsById(question.id()));assertFalse(options.existsById(second.id()));
 }
 @Test void administratorCannotDisableOrDemoteOwnAccount(){
  assertThrows(ValidationException.class,()->admin.deleteUser(actor.getId(),actor));
  assertThrows(ValidationException.class,()->admin.updateUser(actor.getId(),new AdminController.UserWrite("Admin",actor.getEmail(),Role.ESTUDIANTE,null,null,null,true),actor));
 }
}
