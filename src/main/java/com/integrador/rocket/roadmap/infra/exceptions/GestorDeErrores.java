package com.integrador.rocket.roadmap.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GestorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity gestionarErrores() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity errorValidacion(ValidationException ex) {
        var error = ex.getMessage();
        return ResponseEntity.badRequest().body(error);
    }

    //PARA CUANCO FALLA UNA VALIDACIÓN EN LOS RECORDS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity gestionarErroresException(MethodArgumentNotValidException ex) {
        var camposErrores = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(camposErrores.stream().map(DatosErrorValidacion::new));
    }

    //PARA FALLA EN LOS CAMPOS UNICOS EN SQL
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity errorDatosDuplicados(SQLIntegrityConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity gestionarExcepcion(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    public record DatosErrorValidacion(
            String campo,
            String mensaje
    ) {
        public DatosErrorValidacion(FieldError fieldError) {
            this(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
    }


}
