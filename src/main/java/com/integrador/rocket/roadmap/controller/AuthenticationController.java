package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.security.DatosTokenJWT;
import com.integrador.rocket.roadmap.infra.security.TokenService;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserAuthentication;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/login")
    public ResponseEntity iniciarSesion(@RequestBody @Valid UserAuthentication user, HttpServletResponse response){
        var authenticationToken = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        var authentication = authenticationManager.authenticate(authenticationToken);

        var token = tokenService.generarToken((User) authentication.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(2))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<UserDetail> crearUsuario(@RequestBody @Valid UserRegister userRegister, UriComponentsBuilder uriComponentsBuilder) {


        var user = userRepository.save(new User(userRegister, passwordEncoder.encode(userRegister.password())));

        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetail(user));
    }


}
