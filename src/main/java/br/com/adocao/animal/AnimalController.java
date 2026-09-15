package br.com.adocao.animal;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnimalResponse> cadastrar(@Valid @RequestBody AnimalRequest request) {
        AnimalResponse response = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/animais/" + response.id())).body(response);
    }

    @GetMapping
    public List<AnimalResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AnimalResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AnimalResponse atualizar(@PathVariable Long id, @Valid @RequestBody AnimalRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}")
    public AnimalResponse atualizarParcial(@PathVariable Long id, @Valid @RequestBody AnimalPatchRequest request) {
        return service.atualizarParcial(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
