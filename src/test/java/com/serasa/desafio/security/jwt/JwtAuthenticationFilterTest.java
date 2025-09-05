package com.serasa.desafio.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarUnauthorizedQuandoSemToken() throws Exception {
        mockMvc.perform(get("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarUnauthorizedQuandoTokenExpirado() throws Exception {
        
        String expiredToken = "Bearer eyJhbGciOiJIUzI1NiJ9."
                + "eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjAwMDAwMDAwLCJleHAiOjE2MDAwMDAwMDB9."
                + "invalidsignature";

        mockMvc.perform(get("/pessoas")
                        .header("Authorization", expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarUnauthorizedQuandoHeaderInvalido() throws Exception {
        
        String invalidHeader = "InvalidTokenFormat";

        mockMvc.perform(get("/pessoas")
                        .header("Authorization", invalidHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
