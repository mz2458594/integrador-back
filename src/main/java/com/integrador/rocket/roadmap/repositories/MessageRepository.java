package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
