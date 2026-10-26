package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.*;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql=false, properties={"spring.test.database.replace=NONE",
 "spring.datasource.username=sa", "spring.datasource.password=", "logging.level.root=WARN",
 "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
 "spring.datasource.url=jdbc:h2:mem:likes_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(RoadmapEngagementController.class)
class RoadmapEngagementPersistenceTest {
 @Autowired EntityManager em;
 @Autowired RoadmapRepository roadmaps;
 @Autowired RoadmapLikeRepository likes;
 @Autowired RoadmapEngagementController controller;
 User user(String name) {
  var user = new User();
  ReflectionTestUtils.setField(user,"name",name);
  ReflectionTestUtils.setField(user,"email",name+"@example.test");
  ReflectionTestUtils.setField(user,"password","test-only");
  ReflectionTestUtils.setField(user,"createdAt",LocalDateTime.now());
  em.persist(user); return user;
 }
 Roadmap roadmap(User owner, boolean visible) {
  return roadmaps.saveAndFlush(new Roadmap(new RoadmapRegister("Plan","Study","Software",visible,CreationSource.MANUAL),owner));
 }
 @Test void repeatedLikesAndRemovalsAreIdempotentAndPersonalized() {
  var owner=user("Owner"); var voter=user("Voter"); var plan=roadmap(owner,true);
  assertEquals(1,controller.like(plan.getId(),voter).likesCount());
  assertEquals(1,controller.like(plan.getId(),voter).likesCount());
  assertTrue(controller.engagement(plan.getId(),voter).likedByCurrentUser());
  assertFalse(controller.engagement(plan.getId(),owner).likedByCurrentUser());
  assertEquals(0,controller.unlike(plan.getId(),voter).likesCount());
  assertEquals(0,controller.unlike(plan.getId(),voter).likesCount());
 }
 @Test void rankingsAggregatePublicLikesAndExcludePrivateRoadmaps() {
  var first=user("First"); var second=user("Second"); var voter=user("Voter");
  var a=roadmap(first,true); var b=roadmap(first,true); var c=roadmap(second,true);
  controller.like(a.getId(),voter); controller.like(b.getId(),voter); controller.like(c.getId(),voter);
  controller.like(c.getId(),first);
  assertEquals(c.getId(),controller.popular().get(0).getId());
  assertEquals(first.getId(),controller.creators().get(0).getUserId());
  assertEquals(2L,controller.creators().get(0).getLikesCount());
  ReflectionTestUtils.setField(c,"isPublic",false); em.flush();
  assertEquals(2,controller.popular().size());
  assertEquals(1,controller.creators().size());
  assertThrows(AccessDeniedException.class,()->controller.like(c.getId(),voter));
  assertThrows(AccessDeniedException.class,()->controller.engagement(c.getId(),voter));
  assertEquals(2,controller.engagement(c.getId(),second).likesCount());
 }
 @Test void deletionCleansLikesAndMissingRoadmapsAreRejected() {
  var owner=user("Owner"); var voter=user("Voter"); var plan=roadmap(owner,true);
  controller.like(plan.getId(),voter); em.flush(); em.clear();
  roadmaps.deleteById(plan.getId()); roadmaps.flush();
  assertEquals(0,likes.count());
  assertThrows(EntityNotFoundException.class,()->controller.like(plan.getId(),voter));
 }
}
