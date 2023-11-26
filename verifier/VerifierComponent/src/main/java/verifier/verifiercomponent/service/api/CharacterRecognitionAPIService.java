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

    //TODO need a way to generate/disable jwt in ocr
    public Mono<NationalityResponseDTO> performRequest(NationalityRequestDTO nationalityRequestDTO) {
        //TODO create DTO for image
        /*
        request example for ocr to extract text from image:
        curl --location 'http://localhost:5053/ocr' \
            --header 'X-Access-Token: OCR JWT' \
            --header 'Content-Type: application/json' \
            --data '{"image":"representation of img in html(e.g. data:image/png;base64, base64 stuff)"}'
         */
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
