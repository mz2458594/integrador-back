package com.integrador.rocket.roadmap.infra.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class VocationalTestRequiredException extends AccessDeniedException {
    public VocationalTestRequiredException() {
        super("Debes completar el test vocacional antes de crear roadmaps o enviar mensajes.");
    }
}
