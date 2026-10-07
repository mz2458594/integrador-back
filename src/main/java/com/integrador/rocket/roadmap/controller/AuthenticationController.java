package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.security.TokenService;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserAuthentication;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<UserDetail> obtenerUsuarioActual(Authentication authentication){

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserDetail(user));

    }

    @PostMapping("/login")
    public ResponseEntity iniciarSesion(@RequestBody @Valid UserAuthentication user, HttpServletRequest request, HttpServletResponse response){
        var authenticationToken = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        var authentication = authenticationManager.authenticate(authenticationToken);

        var token = tokenService.generarToken((User) authentication.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(2))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity cerrarSesion(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();

    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<UserDetail> crearUsuario(@RequestBody @Valid UserRegister userRegister, UriComponentsBuilder uriComponentsBuilder) {


        var user = userRepository.save(new User(userRegister, passwordEncoder.encode(userRegister.password())));

        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetail(user));
    }


}
