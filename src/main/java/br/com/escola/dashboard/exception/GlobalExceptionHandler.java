package br.com.escola.dashboard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * GlobalExceptionHandler
 *
 * Captura exceções de TODOS os controllers (@Controller e @RestController)
 * e transforma em respostas HTTP organizadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata recurso não encontrado (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        if (isAjaxRequest()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("mensagemErro", "Recurso não encontrado.");
        return "error";
    }

    /**
     * Trata argumentos inválidos (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        log.warn("Argumento inválido: {}", ex.getMessage());

        if (isAjaxRequest()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        return "error";
    }

    /**
     * Trata estados ilegais da aplicação (409)
     */
    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalState(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        log.warn("Estado inválido: {}", ex.getMessage());

        if (isAjaxRequest()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        return "error";
    }

    /**
     * Trata erros de segurança/autorização (403)
     */
    @ExceptionHandler(SecurityException.class)
    public Object handleSecurity(SecurityException ex, RedirectAttributes redirectAttributes) {
        log.warn("Erro de segurança: {}", ex.getMessage());

        if (isAjaxRequest()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        }

        redirectAttributes.addFlashAttribute("mensagemErro", "Acesso negado.");
        return "error";
    }

    /**
     * Trata erros genéricos (500)
     * NUNCA expõe detalhes internos ao usuário.
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Erro interno não tratado", ex);

        if (isAjaxRequest()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor.");
        }

        redirectAttributes.addFlashAttribute("mensagemErro", "Ocorreu um erro inesperado. Tente novamente.");
        return "error";
    }

    private boolean isAjaxRequest() {
        return false;
    }
}
