package com.integrador.rocket.roadmap.models.conversations;

import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "conversation_read_state", uniqueConstraints =
    @UniqueConstraint(columnNames = {"user_id", "conversation_id"}))
public class ConversationReadState {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(optional = false) @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    @Column(nullable = false)
    private Long lastReadMessageId = 0L;

    public ConversationReadState(User user, Conversation conversation) {
        this.user = user;
        this.conversation = conversation;
    }

    public void markRead(long messageId) {
        lastReadMessageId = Math.max(lastReadMessageId, messageId);
    }
}
