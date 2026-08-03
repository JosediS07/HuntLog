package com.huntlog.shared.exception;

import com.huntlog.auth.exception.CredencialesInvalidasException;
import com.huntlog.auth.exception.EmailYaRegistradoException;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.candidatura.exception.TransicionInvalidaException;
import com.huntlog.entrevista.exception.EntrevistaNoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntidadNoEncontradaException.class, CandidaturaNoEncontradaException.class, EntrevistaNoEncontradaException.class})
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
    }

    @ExceptionHandler({ReglaDeNegocioException.class, TransicionInvalidaException.class})
    public ResponseEntity<ErrorResponse> manejarReglaDeNegocio(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("UNPROCESSABLE_ENTITY", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(ServicioExternoNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> manejarServicioExterno(ServicioExternoNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("BAD_GATEWAY", ex.getMessage(), HttpStatus.BAD_GATEWAY.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> manejarEmailYaRegistrado(EmailYaRegistradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", ex.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", "El registro ya existe", HttpStatus.CONFLICT.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioDesactivado(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("FORBIDDEN", "El usuario está desactivado", HttpStatus.FORBIDDEN.value(), LocalDateTime.now()));
    }

    @ExceptionHandler({AuthenticationException.class, CredencialesInvalidasException.class})
    public ResponseEntity<ErrorResponse> manejarAutenticacion(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", "Credenciales inválidas", HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Errores de validación", HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), errores));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExceptionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()));
    }
}
