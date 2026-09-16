package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserList;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.models.users.dto.UserUpdate;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<UserList>> listarUsuarios(@PageableDefault(size = 10) Pageable pageable) {
        var users = userRepository.findAll(pageable).map(UserList::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetail>  detalleUsuario(@PathVariable Long id){
        var user = userRepository.getReferenceById(id);
        return ResponseEntity.ok(new UserDetail(user));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UserDetail> crearUsuario(@RequestBody @Valid UserRegister userRegister, UriComponentsBuilder uriComponentsBuilder){
        var user = userRepository.save(new User(userRegister));

        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetail(user));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<UserDetail> actualizarUsuario(@RequestBody UserUpdate userUpdate, @PathVariable Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el usuario con este ID"));
        user.actualizar(userUpdate);
        return ResponseEntity.ok(new UserDetail(user));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUsuario(@PathVariable Long id){
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro el usuario con este ID"));
        user.eliminar();
        return ResponseEntity.noContent().build();
    }





}
