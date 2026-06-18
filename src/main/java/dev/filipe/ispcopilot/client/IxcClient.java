package dev.filipe.ispcopilot.client;

import dev.filipe.ispcopilot.client.dto.IxcSearchRequest;
import dev.filipe.ispcopilot.client.dto.IxcTr069Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class IxcClient {
    private final RestClient restClient;

    @Value("${ixc.api.url}")
    private String baseUrl;
    @Value("${ixc.api.token}")
    private String token;

    public IxcClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(this.baseUrl)
                .defaultHeader("Authorization", "Bearer " + this.token)
                .defaultHeader("ixcsoft", "listar")
                .build();
    }

    public Optional<IxcTr069Response> getParameterTr069(String login) {
        IxcSearchRequest requestBody = new IxcSearchRequest("login", login, "=");

        List<IxcTr069Response> response = restClient.post()
                .uri("/radusuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(new  ParameterizedTypeReference<>() {});

        return response != null && !response.isEmpty() ? Optional.of(response.getFirst()) : Optional.empty();
    }
}
