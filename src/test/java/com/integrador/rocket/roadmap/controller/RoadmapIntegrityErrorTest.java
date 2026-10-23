package com.integrador.rocket.roadmap.controller;
import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.*;
class RoadmapIntegrityErrorTest {
    @Test void contentTooLongIsNotReportedAsDuplicateConflict() {
        var handler=new GestorDeErrores();
        assertEquals(400,handler.errorDatosDuplicados(new DataIntegrityViolationException("too long",new SQLException("too long","22001"))).getStatusCode().value());
        assertEquals(409,handler.errorDatosDuplicados(new DataIntegrityViolationException("check",new SQLException("check","23514"))).getStatusCode().value());
        assertEquals(409,handler.errorDatosDuplicados(new DataIntegrityViolationException("unique",new SQLException("unique","23505"))).getStatusCode().value());
    }
}
