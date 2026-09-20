package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    public String generarRespuesta(Conversation conversation, String content){
        return "Mensaje de IA";
    }

}
