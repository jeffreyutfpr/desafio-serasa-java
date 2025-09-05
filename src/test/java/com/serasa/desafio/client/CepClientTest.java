package com.serasa.desafio.client;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CepClientTest {

    @Test
    void deveRetornarMapaVazioQuandoViaCepFalhar() {

        RestTemplate restTemplate = mock(RestTemplate.class);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class),
                anyString()
        )).thenThrow(new RestClientException("Falha no ViaCEP"));

        CepClient client = new CepClient(restTemplate);

        Map<String, Object> result = client.buscaEndereco("99999999");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
