package com.serasa.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serasa.desafio.dto.PessoaRequestDto;
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
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("João da Silva")
                .idade(25)
                .cep("01001000")
                .telefone("11999999999")
                .score(800)
                .build();

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
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Maria")
                .idade(30)
                .cep("01001000")
                .score(600)
                .build();

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarPessoasComFiltroPorNome() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Carlos")
                .idade(40)
                .cep("01001000")
                .telefone("11988887777")
                .score(600)
                .build();

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/pessoas")
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("nome", "Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Carlos"));
    }

    @Test
    void deveAtualizarPessoaComSucesso() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Ana")
                .idade(22)
                .cep("01001000")
                .score(500)
                .build();

        String response = mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        PessoaRequestDto updateReq = PessoaRequestDto.builder()
                .nome("Ana Maria")
                .idade(23)
                .cep("01001000")
                .score(700)
                .build();

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
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Pedro")
                .idade(33)
                .cep("01001000")
                .score(300)
                .build();

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
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("nome", "Pedro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void naoDeveCriarPessoaSemNome() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .idade(25)
                .cep("01001000")
                .telefone("1199999999")
                .score(500)
                .build();

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
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste")
                .idade(200)
                .cep("01001000")
                .telefone("1199999999")
                .score(500)
                .build();

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.idade").value("Idade deve ser menor ou igual a 120"));
    }

    @Test
    void naoDeveCriarPessoaComCepInvalido() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Maria")
                .idade(30)
                .cep("123")
                .telefone("1199999999")
                .score(500)
                .build();

        mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.cep").value("CEP deve ter exatamente 8 dígitos"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarPessoaInexistente() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Inexistente")
                .idade(30)
                .cep("01001000")
                .score(400)
                .build();

        mockMvc.perform(put("/pessoas/9999")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    void naoDevePermitirAtualizarComUsuarioUser() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste User")
                .idade(28)
                .cep("01001000")
                .score(400)
                .build();

        String response = mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        PessoaRequestDto updateReq = PessoaRequestDto.builder()
                .nome("User Alterado")
                .idade(29)
                .cep("01001000")
                .score(500)
                .build();

        mockMvc.perform(put("/pessoas/" + id)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoDevePermitirExcluirComUsuarioUser() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste Exclusao")
                .idade(35)
                .cep("01001000")
                .score(500)
                .build();

        String response = mockMvc.perform(post("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/pessoas/" + id)
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirListarComUsuarioAdmin() throws Exception {
        mockMvc.perform(get("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void naoDevePermitirAcessoSemToken() throws Exception {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Sem Token")
                .idade(22)
                .cep("01001000")
                .score(300)
                .build();

        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

}
