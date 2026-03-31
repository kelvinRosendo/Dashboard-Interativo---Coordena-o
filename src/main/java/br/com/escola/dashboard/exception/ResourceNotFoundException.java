package br.com.escola.dashboard.exception;

/**
 * 🔹 ResourceNotFoundException
 * 
 * Essa exceção é usada quando um recurso não é encontrado no sistema.
 * Exemplo: tentar buscar um Card por ID que não existe no banco.
 */
public class ResourceNotFoundException extends RuntimeException {

    // 🔹 Construtor padrão com mensagem personalizada
    public ResourceNotFoundException(String message) {
        super(message);
    }
}