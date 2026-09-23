package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class AiService {

    private final ChatModel chatModel;

    public AiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    public String generarRespuesta(Conversation conversation, String content) {
        String prompt = """
                Eres un asistente académico que ayuda a estudiante universitarios 
                a construir rutas de aprendizaje (roadmaps). Responde de forma clara y concisa
                
                Mensaje del estudiante: %s
                """.formatted(content);

        return chatModel.call(prompt);
    }

}
