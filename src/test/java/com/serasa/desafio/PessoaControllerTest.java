package com.serasa.desafio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serasa.desafio.dto.PessoaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setup() throws Exception {

        String loginAdmin = """
                {"username":"admin","password":"admin123"}
                """;

        var resAdmin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginAdmin))
                .andExpect(status().isOk())
                .andReturn();

        tokenAdmin = objectMapper.readTree(resAdmin.getResponse().getContentAsString()).get("token").asText();

        String loginUser = """
                {"username":"user","password":"user123"}
                """;

        var resUser = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUser))
                .andExpect(status().isOk())
                .andReturn();

        tokenUser = objectMapper.readTree(resUser.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void deveCriarPessoaComSucesso() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("João da Silva");
        req.setIdade(25);
        req.setCep("01001000");
        req.setTelefone("11999999999");
        req.setScore(800);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.scoreDescricao").value("Recomendável"));
    }

    @Test
    void naoDevePermitirCriacaoComUsuarioUser() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Maria");
        req.setIdade(30);
        req.setCep("01001000");
        req.setScore(600);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarPessoasComFiltroPorNome() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Carlos");
        req.setIdade(40);
        req.setCep("01001000");
        req.setTelefone("11988887777");
        req.setScore(600);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/pessoas")
                        .param("nome", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Carlos"));
    }

    @Test
    void deveAtualizarPessoaComSucesso() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Ana");
        req.setIdade(22);
        req.setCep("01001000");
        req.setScore(500);

        String response = mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        PessoaRequest updateReq = new PessoaRequest();
        updateReq.setNome("Ana Maria");
        updateReq.setIdade(23);
        updateReq.setCep("01001000");
        updateReq.setScore(700);

        mockMvc.perform(put("/pessoas/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Maria"))
                .andExpect(jsonPath("$.idade").value(23))
                .andExpect(jsonPath("$.scoreDescricao").value("Aceitável"));
    }

    @Test
    void deveExcluirPessoaLogicamente() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Pedro");
        req.setIdade(33);
        req.setCep("01001000");
        req.setScore(300);

        String response = mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/pessoas/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/pessoas")
                        .param("nome", "Pedro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void naoDeveCriarPessoaSemNome() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setIdade(25);
        req.setCep("01001000");
        req.setTelefone("1199999999");
        req.setScore(500);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de validação"))
                .andExpect(jsonPath("$.details.nome").value("Nome é obrigatório"));
    }

    @Test
    void naoDeveCriarPessoaComIdadeInvalida() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Teste");
        req.setIdade(200); // inválido
        req.setCep("01001000");
        req.setTelefone("1199999999");
        req.setScore(500);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.idade").value("Idade deve ser menor ou igual a 120"));
    }

    @Test
    void naoDeveCriarPessoaComCepInvalido() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Maria");
        req.setIdade(30);
        req.setCep("123"); // inválido
        req.setTelefone("1199999999");
        req.setScore(500);

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.cep").value("CEP deve ter exatamente 8 dígitos"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarPessoaInexistente() throws Exception {
        PessoaRequest req = new PessoaRequest();
        req.setNome("Inexistente");
        req.setIdade(30);
        req.setCep("01001000");
        req.setScore(400);

        mockMvc.perform(put("/pessoas/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

}
