package com.integrador.rocket.roadmap.models.conversations;

import com.integrador.rocket.roadmap.models.comments.dto.CommentUpdate;
import com.integrador.rocket.roadmap.models.conversations.dto.ConversationRegister;
import com.integrador.rocket.roadmap.models.conversations.dto.ConversationUpdate;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conversations")
@EntityListeners(AuditingEntityListener.class)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "conversation_participant",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();


    public Conversation(@Valid ConversationRegister conversationRegister, List<User> participants) {
        this.type = conversationRegister.type();
        this.participants = participants;
    }

    public void actualizar(ConversationUpdate conversationUpdate) {

    }
}
