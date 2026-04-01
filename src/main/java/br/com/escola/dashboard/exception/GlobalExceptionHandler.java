package br.com.escola.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 🔹 GlobalExceptionHandler
 *
 * Essa classe captura exceções da aplicação inteira
 * e transforma em respostas HTTP organizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 🔹 Trata erro de recurso não encontrado
     *
     * Exemplo: quando um card não existe
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(ex.getMessage());       // mensagem personalizada
    }

    /**
     * 🔹 Trata erros genéricos (qualquer erro não tratado)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body("Erro interno no servidor: " + ex.getMessage());
    }
}