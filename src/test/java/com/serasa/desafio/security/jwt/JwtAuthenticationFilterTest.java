package com.serasa.desafio.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenAdmin;

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
    }

    @Test
    void devePermitirAcessoComTokenValido() throws Exception {
        mockMvc.perform(get("/pessoas")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401QuandoHeaderAusente() throws Exception {
        mockMvc.perform(get("/pessoas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        mockMvc.perform(get("/pessoas")
                        .header("Authorization", "Bearer tokenInvalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoTokenExpirado() throws Exception {
        String expiredToken = jwtUtil.generateToken("admin", -1000);

        mockMvc.perform(get("/pessoas")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
