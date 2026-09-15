package br.com.adocao.animal;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnimalService {

    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public AnimalResponse cadastrar(AnimalRequest request) {
        Animal animal = new Animal(
                request.nome(), request.especie(), request.raca(), request.idade(), request.sexo(),
                request.porte(), request.descricao(), request.disponivelParaAdocao()
        );
        return AnimalResponse.from(repository.save(animal));
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> listar() {
        return repository.findAll().stream().map(AnimalResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponse buscarPorId(Long id) {
        return AnimalResponse.from(buscarEntidade(id));
    }

    public AnimalResponse atualizar(Long id, AnimalRequest request) {
        Animal animal = buscarEntidade(id);
        animal.atualizar(
                request.nome(), request.especie(), request.raca(), request.idade(), request.sexo(),
                request.porte(), request.descricao(), request.disponivelParaAdocao()
        );
        return AnimalResponse.from(repository.save(animal));
    }

    public AnimalResponse atualizarParcial(Long id, AnimalPatchRequest request) {
        Animal animal = buscarEntidade(id);
        animal.atualizar(
                valorOuAtual(request.nome(), animal.getNome()),
                valorOuAtual(request.especie(), animal.getEspecie()),
                valorOuAtual(request.raca(), animal.getRaca()),
                valorOuAtual(request.idade(), animal.getIdade()),
                valorOuAtual(request.sexo(), animal.getSexo()),
                valorOuAtual(request.porte(), animal.getPorte()),
                valorOuAtual(request.descricao(), animal.getDescricao()),
                valorOuAtual(request.disponivelParaAdocao(), animal.getDisponivelParaAdocao())
        );
        return AnimalResponse.from(repository.save(animal));
    }

    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Animal buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(AnimalNaoEncontradoException::new);
    }

    private <T> T valorOuAtual(T valorRecebido, T valorAtual) {
        return valorRecebido == null ? valorAtual : valorRecebido;
    }
}
