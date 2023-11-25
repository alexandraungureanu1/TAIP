package verifier.verifiercomponent.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.dataprovider.StudentRequestDTO;

@RestController
public class DataProviderAPIService {

    private final WebClient webClient;

    public DataProviderAPIService(@Qualifier("dataProviderAPI") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Boolean> performRequest(StudentRequestDTO studentRequestDTO) {
        return webClient.post()
                .uri("/test-student")
                .bodyValue(studentRequestDTO)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Custom message", e)));
    }
}
