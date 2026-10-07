package com.integrador.rocket.roadmap.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestorDeErrores {

    private static final Logger audit = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Logger log = LoggerFactory.getLogger(GestorDeErrores.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity autenticacion(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorApi("Credenciales inválidas"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accesoDenegado(AccessDeniedException e, HttpServletRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        audit.warn("ACCESS DENIED user={} ip={} {} {}",
                auth != null ? limpiar(auth.getName()) : "anonimo",
                request.getRemoteAddr(), request.getMethod(), limpiar(request.getRequestURI())
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorApi("No tienes permiso para realizar esta accion"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity gestionarErrores() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity errorValidacion(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ErrorApi(ex.getMessage()));
    }

    //PARA CUANCO FALLA UNA VALIDACIÓN EN LOS RECORDS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity gestionarErroresException(MethodArgumentNotValidException ex) {
        var camposErrores = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(camposErrores.stream().map(DatosErrorValidacion::new));
    }

    //PARA FALLA EN LOS CAMPOS UNICOS EN SQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity errorDatosDuplicados(DataIntegrityViolationException ex) {
        log.warn("Violacion de seguridad : {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorApi("La operación no es válida: el dato ya existe o esta referenciado"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity gestionarExcepcion(Exception ex, HttpServletRequest request) {

        if (ex instanceof ErrorResponse errorResponse){
            return ResponseEntity.status(errorResponse.getStatusCode()).body(new ErrorApi(errorResponse.getBody().getDetail()));
        }

        log.error("Error no controlado en {} {}", request.getMethod(), limpiar(request.getRequestURI()), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorApi("Error interno del servidor"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity jsonInvalido(HttpMessageNotReadableException ex){
        return ResponseEntity.badRequest().body(new ErrorApi("El cuerpo de la peticion no es valido"));
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity handleRuntimeException(RuntimeException e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
//    }


    public static String limpiar(String valor){
        return valor == null ? "null": valor.replaceAll("[\\r\\n]", "_");
    }

    public record ErrorApi(String mensaje){

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
