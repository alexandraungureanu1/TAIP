package verifier.verifiercomponent.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import verifier.verifiercomponent.dto.ocr.OcrRequestDTO;
import verifier.verifiercomponent.dto.ocr.OcrTemplateDTO;
import verifier.verifiercomponent.dto.ocr.OcrTemplateResponseDTO;
import verifier.verifiercomponent.helpers.ImageToBase64Converter;
import verifier.verifiercomponent.service.api.CharacterRecognitionAPIService;

import java.nio.file.Files;
import java.nio.file.Paths;

@org.springframework.stereotype.Service
@AllArgsConstructor
public class TemplateService {
    private final CharacterRecognitionAPIService characterRecognitionAPIService;

    public Mono<ResponseEntity<OcrTemplateResponseDTO>> createTemplate(String templatePath, String imagePath) {
        return Mono.fromCallable(() -> Files.readAllBytes(Paths.get(templatePath)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(String::new)
                .flatMap(jsonContent -> {
                    Gson gson = new Gson();
                    OcrTemplateDTO ocrTemplateDTO = gson.fromJson(jsonContent, OcrTemplateDTO.class);
                    return Mono.fromCallable(() -> ImageToBase64Converter.encodeImageToBase64Uri(imagePath))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(encodedImage -> {
                                if (!ocrTemplateDTO.getPages().isEmpty()) {
                                    ocrTemplateDTO.getPages().get(0).setImage(encodedImage);
                                }
                                return ocrTemplateDTO;
                            });
                })
                .flatMap(ocrTemplateDTO -> characterRecognitionAPIService.createTemplate(ocrTemplateDTO, OcrTemplateResponseDTO.class))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
