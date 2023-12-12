package verifier.verifiercomponent.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.ocr.OcrRequestDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;

@RestController
public class CharacterRecognitionAPIService {

    private final WebClient webClient;

    public CharacterRecognitionAPIService(@Qualifier("characterRecognitionAPI") WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<T> performRequest(OcrRequestDTO ocrRequestDTO, Class<T> classObject) {
        return webClient.post()
                .uri("/ocr")
                .bodyValue(ocrRequestDTO)
                .retrieve()
                .bodyToMono(classObject)
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Custom message", e));
                });
    }
}
