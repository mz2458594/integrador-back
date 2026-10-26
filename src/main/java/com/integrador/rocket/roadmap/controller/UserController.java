package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.Role;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserList;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.models.users.dto.UserUpdate;
import com.integrador.rocket.roadmap.repositories.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/user")
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
    public ResponseEntity<UserDetail> actualizarUsuario(@org.springframework.security.core.annotation.AuthenticationPrincipal User actor, @RequestBody UserUpdate userUpdate, @PathVariable Long id) {
        boolean admin = actor != null && actor.getRole() == Role.ADMINISTRADOR;
        if (actor == null || (!admin && (!id.equals(actor.getId()) || userUpdate.role() != null)))
            throw new org.springframework.security.access.AccessDeniedException("No puedes modificar este usuario ni su rol");
        var user = userRepository.getReferenceById(id);
        if (id.equals(actor.getId()) && userUpdate.role() != null && userUpdate.role() != user.getRole())
            throw new ValidationException("No puedes cambiar tu propio rol");
        var password = user.getPassword();
        if (userUpdate.password() != null) {
            password = passwordEncoder.encode(userUpdate.password());
        }

        user.actualizar(userUpdate, password);
        return ResponseEntity.ok(new UserDetail(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUsuario(@org.springframework.security.core.annotation.AuthenticationPrincipal User actor, @PathVariable Long id) {
        if (actor != null && id.equals(actor.getId())) throw new ValidationException("No puedes desactivar tu propia cuenta");
        var user = userRepository.findById(id).orElseThrow(jakarta.persistence.EntityNotFoundException::new);
        user.eliminar();
        return ResponseEntity.noContent().build();
    }


}
