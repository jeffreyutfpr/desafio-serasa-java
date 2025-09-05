package com.serasa.desafio.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class CepClient {

    private final RestTemplate restTemplate;
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public CepClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> buscaEndereco(String cep) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    VIA_CEP_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    },
                    cep
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (RestClientException e) {
            log.warn("Falha ao consultar ViaCEP para CEP {}: {}", cep, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
