package br.com.adocao.animal;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record AnimalPatchRequest(
        @Pattern(regexp = "(?s).*\\S.*", message = "não pode ficar em branco") String nome,
        @Pattern(regexp = "(?s).*\\S.*", message = "não pode ficar em branco") String especie,
        String raca,
        @PositiveOrZero(message = "não pode ser negativo") Integer idade,
        @Pattern(regexp = "(?s).*\\S.*", message = "não pode ficar em branco") String sexo,
        @Pattern(regexp = "(?s).*\\S.*", message = "não pode ficar em branco") String porte,
        String descricao,
        Boolean disponivelParaAdocao
) {
}
