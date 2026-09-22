package com.integrador.rocket.roadmap.models.users;

import com.integrador.rocket.roadmap.models.ai.AiInteraction;
import com.integrador.rocket.roadmap.models.comments.Comment;
import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import com.integrador.rocket.roadmap.models.users.dto.UserRegister;
import com.integrador.rocket.roadmap.models.users.dto.UserUpdate;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;

    private boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "participants")
    private List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Message> messages;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VocationalTestResult vocationalTestResults;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Roadmap> roadmaps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRoadmap> userRoadmaps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserStepProgress> userStepProgresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AiInteraction> aiInteractions;

    public User(UserRegister userRegister, String password) {
        this.name = userRegister.name();
        this.email = userRegister.email();
        if (userRegister.role() != null) {
            this.role = userRegister.role();
        } else {
            this.role = Role.ESTUDIANTE;
        }
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        this.role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission))
        );

        return authorities;

    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public void actualizar(UserUpdate userUpdate, String password) {

        if (userUpdate.name() != null) {
            this.name = userUpdate.name();
        }

        if (userUpdate.email() != null) {
            this.email = userUpdate.email();
        }

        if (userUpdate.role() != null) {
            this.role = userUpdate.role();
        }
        if (userUpdate.password() != null) {
            this.password = password;
        }
    }

    public void eliminar() {
        this.isActive = false;
    }

}
