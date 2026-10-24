package com.integrador.rocket.roadmap.repositories;
import com.integrador.rocket.roadmap.models.conversations.ConversationReadState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConversationReadStateRepository extends JpaRepository<ConversationReadState, Long> {
    List<ConversationReadState> findAllByUserId(Long userId);
    Optional<ConversationReadState> findByUserIdAndConversationId(Long userId, Long conversationId);
}
