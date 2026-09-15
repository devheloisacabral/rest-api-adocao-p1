package br.com.adocao.animal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    private static final AnimalRequest ANIMAL_VALIDO = new AnimalRequest(
            "Luna", "Cachorro", "Vira-lata", 3, "FEMEA", "MEDIO", "Dócil e vacinada", true
    );

    @Mock
    private AnimalRepository repository;

    @InjectMocks
    private AnimalService service;

    @Test
    void deveCadastrarAnimalComDadosValidos() {
        when(repository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse response = service.cadastrar(ANIMAL_VALIDO);

        assertThat(response.nome()).isEqualTo("Luna");
        assertThat(response.especie()).isEqualTo("Cachorro");
        assertThat(response.disponivelParaAdocao()).isTrue();
        verify(repository).save(any(Animal.class));
    }

    @Test
    void deveRejeitarCamposObrigatoriosAusentesOuInvalidos() {
        AnimalRequest request = new AnimalRequest(" ", null, null, -1, "", "", null, null);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(request))
                    .extracting(violacao -> violacao.getPropertyPath().toString())
                    .containsExactlyInAnyOrder(
                            "nome", "especie", "idade", "sexo", "porte", "disponivelParaAdocao"
                    );
        }

        verifyNoInteractions(repository);
    }

    @Test
    void deveListarAnimaisCadastrados() {
        when(repository.findAll()).thenReturn(List.of(criarAnimal("Luna"), criarAnimal("Rex")));

        List<AnimalResponse> response = service.listar();

        assertThat(response).extracting(AnimalResponse::nome).containsExactly("Luna", "Rex");
    }

    @Test
    void deveConsultarAnimalPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarAnimal("Luna")));

        AnimalResponse response = service.buscarPorId(1L);

        assertThat(response.nome()).isEqualTo("Luna");
    }

    @Test
    void deveFalharAoConsultarAnimalInexistente() {
        when(repository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(9999L))
                .isInstanceOf(AnimalNaoEncontradoException.class);
    }

    @Test
    void deveAtualizarAnimalExistente() {
        Animal animal = criarAnimal("Luna");
        when(repository.findById(1L)).thenReturn(Optional.of(animal));
        when(repository.save(animal)).thenReturn(animal);
        AnimalRequest request = new AnimalRequest(
                "Luna Atualizada", "Cachorro", "Vira-lata", 4, "FEMEA", "MEDIO", "Adotada", false
        );

        AnimalResponse response = service.atualizar(1L, request);

        assertThat(response.nome()).isEqualTo("Luna Atualizada");
        assertThat(response.idade()).isEqualTo(4);
        assertThat(response.disponivelParaAdocao()).isFalse();
        verify(repository).save(animal);
    }

    @Test
    void deveFalharAoAtualizarAnimalInexistente() {
        when(repository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(9999L, ANIMAL_VALIDO))
                .isInstanceOf(AnimalNaoEncontradoException.class);
    }

    @Test
    void devePreservarCamposOmitidosNaAtualizacaoParcial() {
        Animal animal = criarAnimal("Luna");
        when(repository.findById(1L)).thenReturn(Optional.of(animal));
        when(repository.save(animal)).thenReturn(animal);
        AnimalPatchRequest request = new AnimalPatchRequest(
                null, null, null, 4, null, null, null, null
        );

        AnimalResponse response = service.atualizarParcial(1L, request);

        assertThat(response.nome()).isEqualTo("Luna");
        assertThat(response.idade()).isEqualTo(4);
        assertThat(response.especie()).isEqualTo("Cachorro");
        assertThat(response.disponivelParaAdocao()).isTrue();
        verify(repository).save(animal);
    }

    @Test
    void deveExcluirAnimalExistente() {
        Animal animal = criarAnimal("Luna");
        when(repository.findById(1L)).thenReturn(Optional.of(animal));

        service.excluir(1L);

        verify(repository).delete(animal);
    }

    @Test
    void deveFalharAoExcluirAnimalInexistente() {
        when(repository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(9999L))
                .isInstanceOf(AnimalNaoEncontradoException.class);
    }

    private Animal criarAnimal(String nome) {
        return new Animal(nome, "Cachorro", "Vira-lata", 3, "FEMEA", "MEDIO", "Dócil", true);
    }
}
