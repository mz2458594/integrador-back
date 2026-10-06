package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("""
            SELECT c FROM Conversation c
            JOIN c.participants p1
            JOIN c.participants p2
            WHERE p1.id = :userId1 AND p2.id = :userId2
            AND SIZE(c.participants) = 2
            """)
    Optional<Conversation> findDirectConversationBetween(Long userId1, Long userId2);

    Optional<Conversation> findFirstByTypeAndParticipants_IdOrderByCreatedAtDesc(
            ConversationType type,
            Long participantId
    );

    @Query("""
            SELECT c FROM Conversation c
            JOIN c.participants p1
            WHERE p1.id = :idUser
            """)
    Page<Conversation> findAllByUserId(Pageable pageable, Long idUser);
}
