package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.*;
import com.integrador.rocket.roadmap.models.users.dto.*;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import com.integrador.rocket.roadmap.infra.security.*;
import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminSecurityBoundaryTest {
 @AfterEach void clear(){SecurityContextHolder.clearContext();}
 @Test void publicRegistrationCannotChooseAdministratorRole() throws Exception {
  var users=mock(UserRepository.class);var encoder=mock(PasswordEncoder.class);
  when(encoder.encode(any())).thenReturn("hashed-password");
  when(users.save(any(User.class))).thenAnswer(call->{User user=call.getArgument(0);ReflectionTestUtils.setField(user,"id",1L);return user;});
  var controller=new AuthenticationController();ReflectionTestUtils.setField(controller,"userRepository",users);ReflectionTestUtils.setField(controller,"passwordEncoder",encoder);
  var mvc=MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GestorDeErrores()).build();
  mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New\",\"email\":\"new@example.test\",\"password\":\"Strong1!x\",\"role\":\"ADMINISTRADOR\"}"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("ESTUDIANTE"));
 }
 @Test void ordinaryUserCannotChangeOwnRoleOrAnotherProfile(){
  var controller=new UserController();var actor=mock(User.class);when(actor.getId()).thenReturn(1L);when(actor.getRole()).thenReturn(Role.ESTUDIANTE);
  assertThrows(AccessDeniedException.class,()->controller.actualizarUsuario(actor,new UserUpdate(null,null,Role.ADMINISTRADOR,null,null,null),1L));
  assertThrows(AccessDeniedException.class,()->controller.actualizarUsuario(actor,new UserUpdate("Other",null,null,null,null,null),2L));
 }
 @Test void disabledAccountCannotReuseAnExistingSession() throws Exception {
  var users=mock(UserRepository.class);var tokens=mock(TokenService.class);var user=new User(new UserRegister("Test","test@example.test",Role.ADMINISTRADOR,"Strong1!x"),"hash");user.eliminar();
  when(tokens.getSubject("existing-token")).thenReturn("test@example.test");when(users.findByEmail("test@example.test")).thenReturn(user);
  var filter=new SecurityFilter();ReflectionTestUtils.setField(filter,"tokenService",tokens);ReflectionTestUtils.setField(filter,"userRepository",users);
  var request=new MockHttpServletRequest();request.setCookies(new Cookie("token","existing-token"));var chain=mock(FilterChain.class);
  filter.doFilter(request,new MockHttpServletResponse(),chain);assertNull(SecurityContextHolder.getContext().getAuthentication());verify(chain).doFilter(any(),any());
 }
}
