package br.edu.ifrn.chamados.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> handleEntityNotFoundException(
            EntityNotFoundException exception,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(), // Ex: "Setor não encontrado"
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationExceptions(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dados inválidos");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                request.getRequestURI());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> handleRegraNegocioException(
            RegraNegocioException exception,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Violação de integridade de dados: registro duplicado",
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleException(
            Exception exception,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor.",
                request.getRequestURI());
    }

    private ResponseEntity<StandardError> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);

        return ResponseEntity.status(status).body(error);
    }
}
