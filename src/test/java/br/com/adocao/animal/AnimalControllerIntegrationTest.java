package br.com.adocao.animal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnimalControllerIntegrationTest {

    private static final String ANIMAL_VALIDO = """
            {
              "nome": "Luna",
              "especie": "Cachorro",
              "raca": "Vira-lata",
              "idade": 3,
              "sexo": "FEMEA",
              "porte": "MEDIO",
              "descricao": "Dócil e vacinada",
              "disponivelParaAdocao": true
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparDados() {
        repository.deleteAll();
    }

    @Test
    void deveCadastrarAnimalComDadosValidos() throws Exception {
        mockMvc.perform(post("/animais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ANIMAL_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Luna"))
                .andExpect(jsonPath("$.disponivelParaAdocao").value(true));
    }

    @Test
    void deveRejeitarCadastroComCamposObrigatoriosAusentesOuInvalidos() throws Exception {
        String animalInvalido = """
                {
                  "nome": " ",
                  "especie": null,
                  "idade": -1,
                  "sexo": "",
                  "porte": "",
                  "disponivelParaAdocao": null
                }
                """;

        mockMvc.perform(post("/animais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(animalInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.especie").exists())
                .andExpect(jsonPath("$.campos.idade").exists())
                .andExpect(jsonPath("$.campos.sexo").exists())
                .andExpect(jsonPath("$.campos.porte").exists())
                .andExpect(jsonPath("$.campos.disponivelParaAdocao").exists());
    }

    @Test
    void deveListarAnimaisCadastrados() throws Exception {
        cadastrarAnimal();
        cadastrarAnimal("Rex");

        mockMvc.perform(get("/animais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.nome == 'Luna')]").exists())
                .andExpect(jsonPath("$[?(@.nome == 'Rex')]").exists());
    }

    @Test
    void deveConsultarAnimalPorIdExistente() throws Exception {
        long id = cadastrarAnimal();

        mockMvc.perform(get("/animais/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Luna"));
    }

    @Test
    void deveRetornar404AoConsultarAnimalInexistente() throws Exception {
        mockMvc.perform(get("/animais/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Animal não encontrado"));
    }

    @Test
    void deveAtualizarAnimalExistente() throws Exception {
        long id = cadastrarAnimal();
        String animalAtualizado = ANIMAL_VALIDO.replace("Luna", "Luna Silva");

        mockMvc.perform(put("/animais/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(animalAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Luna Silva"));
    }

    @Test
    void deveRetornar404AoAtualizarAnimalInexistente() throws Exception {
        mockMvc.perform(put("/animais/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ANIMAL_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Animal não encontrado"));
    }

    @Test
    void deveAtualizarSomenteOsCamposEnviadosNoPatch() throws Exception {
        long id = cadastrarAnimal();
        String alteracaoParcial = """
                {
                  "nome": "Luna Atualizada",
                  "disponivelParaAdocao": false
                }
                """;

        mockMvc.perform(patch("/animais/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alteracaoParcial))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Luna Atualizada"))
                .andExpect(jsonPath("$.especie").value("Cachorro"))
                .andExpect(jsonPath("$.raca").value("Vira-lata"))
                .andExpect(jsonPath("$.idade").value(3))
                .andExpect(jsonPath("$.sexo").value("FEMEA"))
                .andExpect(jsonPath("$.porte").value("MEDIO"))
                .andExpect(jsonPath("$.descricao").value("Dócil e vacinada"))
                .andExpect(jsonPath("$.disponivelParaAdocao").value(false));
    }

    @Test
    void deveRejeitarCamposInvalidosNoPatch() throws Exception {
        mockMvc.perform(patch("/animais/{id}", cadastrarAnimal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" \",\"idade\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.idade").exists());
    }

    @Test
    void deveRetornar404AoAplicarPatchEmAnimalInexistente() throws Exception {
        mockMvc.perform(patch("/animais/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Luna Atualizada\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Animal não encontrado"));
    }

    @Test
    void deveExcluirAnimalExistente() throws Exception {
        long id = cadastrarAnimal();

        mockMvc.perform(delete("/animais/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/animais/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoExcluirAnimalInexistente() throws Exception {
        mockMvc.perform(delete("/animais/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Animal não encontrado"));
    }

    private long cadastrarAnimal() throws Exception {
        return cadastrarAnimal("Luna");
    }

    private long cadastrarAnimal(String nome) throws Exception {
        String conteudo = ANIMAL_VALIDO.replace("Luna", nome);
        MvcResult resultado = mockMvc.perform(post("/animais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conteudo))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode resposta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return resposta.get("id").asLong();
    }
}
