package br.com.adocao.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AnimalRequest(
        @NotBlank(message = "é obrigatório") String nome,
        @NotBlank(message = "é obrigatório") String especie,
        String raca,
        @NotNull(message = "é obrigatório") @PositiveOrZero(message = "não pode ser negativo") Integer idade,
        @NotBlank(message = "é obrigatório") String sexo,
        @NotBlank(message = "é obrigatório") String porte,
        String descricao,
        @NotNull(message = "é obrigatório") Boolean disponivelParaAdocao
) {
}
