package verifier.verifiercomponent.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.ocr.NationalityRequestDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;

@RestController
public class CharacterRecognitionAPIService {

    private final WebClient webClient;

    public CharacterRecognitionAPIService(@Qualifier("characterRecognitionAPI") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<NationalityResponseDTO> performRequest(NationalityRequestDTO nationalityRequestDTO) {
        return webClient.post()
                .uri("/test-nationality")
                .bodyValue(nationalityRequestDTO)
                .retrieve()
                .bodyToMono(NationalityResponseDTO.class)
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Custom message", e));
                });
    }
}
