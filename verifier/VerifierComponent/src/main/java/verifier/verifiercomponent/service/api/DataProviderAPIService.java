package verifier.verifiercomponent.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.dataprovider.StudentRequestDTO;

import java.util.Objects;

@RestController
public class DataProviderAPIService {

    private final WebClient webClient;

    public DataProviderAPIService(@Qualifier("dataProviderAPI") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> performRequest(StudentRequestDTO studentRequestDTO) {
        Mono<String> stringMono = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/requestData")
                        .queryParam("source", "REST_API")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":\"33\"}") //TODO figure out body
                .retrieve()
                .bodyToMono(String.class) //TODO figure this out as well
                //.map(Object::toString)
                .onErrorResume(e -> Mono.error(new RuntimeException("Custom message", e)));
        return stringMono;
    }
}
