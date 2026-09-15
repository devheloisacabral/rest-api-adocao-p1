package br.com.adocao.api;

import java.util.Map;
import java.util.TreeMap;

import br.com.adocao.animal.AnimalNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AnimalNaoEncontradoException.class)
    public ResponseEntity<ApiError> tratarAnimalNaoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("Animal não encontrado", Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarDadosInvalidos(MethodArgumentNotValidException exception) {
        Map<String, String> campos = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erro ->
                campos.putIfAbsent(erro.getField(), erro.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(new ApiError("Dados inválidos", campos));
    }
}
