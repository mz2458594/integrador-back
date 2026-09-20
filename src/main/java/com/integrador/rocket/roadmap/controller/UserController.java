package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserList;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.models.users.dto.UserUpdate;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<UserList>> listarUsuarios(@PageableDefault(size = 10) Pageable pageable) {
        var users = userRepository.findAll(pageable).map(UserList::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetail> detalleUsuario(@PathVariable Long id) {
        var user = userRepository.getReferenceById(id);
        return ResponseEntity.ok(new UserDetail(user));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<UserDetail> actualizarUsuario(@RequestBody UserUpdate userUpdate, @PathVariable Long id) {
        var user = userRepository.getReferenceById(id);
        var password = user.getPassword();
        if (userUpdate.password() != null) {
            password = passwordEncoder.encode(userUpdate.password());
        }

        user.actualizar(userUpdate, password);
        return ResponseEntity.ok(new UserDetail(user));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUsuario(@PathVariable Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el usuario con este ID"));
        user.eliminar();
        return ResponseEntity.noContent().build();
    }


}
