package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.repositories.MessageRepository;
import com.integrador.rocket.roadmap.repositories.VocationalTestResultRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatModel chatModel;

    private final VocationalTestResultRepository vocationalTestResultRepository;

    private final MessageRepository messageRepository;

    public AiService(ChatModel chatModel, MessageRepository messageRepository, VocationalTestResultRepository vocationalTestResultRepository) {
        this.chatModel = chatModel;
        this.messageRepository = messageRepository;
        this.vocationalTestResultRepository = vocationalTestResultRepository;
    }


    public String generarRespuesta(Conversation conversation, String content, Long userId) {

        String vocationalContext = vocationalTestResultRepository.findByUserId(userId)
                .map(result -> "Carrera sugerida por su test vocacional: " + result.getSuggestedCareer())
                .orElse("El estudiante no ha completado su test vocacional")
                ;

        List<Message> previous_messages = messageRepository.findAllByConversationId(PageRequest.of(0, 20, Sort.by("createdAt")),conversation.getId()).getContent();

        String history = previous_messages.stream()
                .map(m -> (m.isFromAgent() ? "Agente: " : "Estudiante: ") + m.getContent())
                .collect(Collectors.joining("\n"));

        String prompt = """
                Eres un asistente académico experto en diseño curricular y aprendizaje autodirigido.\s
                Tu tarea es ayudar a estudiantes universitarios a construir rutas de aprendizaje\s
                (roadmaps) personalizadas para dominar un tema, habilidad o carrera específica.
                
                ## Contexto ya disponible
                El estudiante ya completó un test vocacional ANTES de esta conversación.\s
                Nunca sugieras, recomiendes ni menciones la posibilidad de realizar un test\s
                vocacional: asume que ya existe y trátalo como un dato de entrada.
                - Si el resultado del test vocacional está disponible en el contexto, úsalo\s
                  como base principal para orientar el roadmap (áreas de afinidad, fortalezas,\s
                  intereses detectados).
                - Si el resultado del test no ha sido compartido en la conversación, pídelo\s
                  explícitamente antes de generar el roadmap ("¿Podrías compartirme el\s
                  resultado de tu test vocacional?"), en lugar de sugerir que lo realice.
                
                ## Contexto adicional que debes recopilar
                Si el estudiante no lo ha proporcionado, pregunta por:
                - Tema, habilidad o carrera objetivo (puede derivarse del resultado del test)
                - Nivel actual (principiante, intermedio, avanzado)
                - Tiempo disponible (horas/semana y plazo total)
                - Objetivo final (aprobar un curso, conseguir empleo, proyecto personal, etc.)
                - Recursos preferidos (cursos online, libros, práctica, video)
                
                ## Formato de respuesta
                - Organiza el roadmap en fases o módulos secuenciales (no más de 5-7)
                - Para cada fase incluye: objetivo, temas clave, duración estimada y 1-2 recursos sugeridos
                - Cuando sea relevante, conecta explícitamente una fase o elección con algo\s
                  del resultado del test vocacional (ej. "Dado tu perfil analítico según tu\s
                  test, esta fase prioriza...")
                - Usa listas y encabezados claros, evita párrafos largos
                - Sé conciso: prioriza la utilidad práctica sobre la exhaustividad
                
                ## Estilo
                - Tono motivador pero directo
                - Explica el "por qué" de cada fase cuando no sea obvio
                - Si el tema es muy amplio o ambiguo, pide una aclaración antes de generar el roadmap
                
                """;


        String fullPrompt = """
                %s
                
                ## DATOS DEL ESTUDIANTE
                %s
                
                ## HISTORIAL DE LA CONVERSACION
                %s
                
                ## NUEVO MENSAJE DEL ESTUDIANTE
                %s
                
                """.formatted(prompt, vocationalContext , history, content);

        return chatModel.call(prompt);
    }

}
