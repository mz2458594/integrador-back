package com.integrador.rocket.roadmap.models.messages;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.messages.dto.MessageUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean isFromAgent = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Message(String content, Conversation conversation, User user) {
        this.content = content;
        this.conversation = conversation;
        if (user == null) {
            this.isFromAgent = true;
        }
        this.user = user;
    }


    public void actualizar(MessageUpdate messageUpdate) {
        if (messageUpdate.content() != null) {
            this.content = messageUpdate.content();
        }
    }
}
