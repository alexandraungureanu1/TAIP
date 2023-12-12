package verifier.verifiercomponent.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.dataprovider.StudentRequestDTO;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.OcrRequestDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;
import verifier.verifiercomponent.dto.ocr.StudentResponseDTO;
import verifier.verifiercomponent.service.api.CharacterRecognitionAPIService;
import verifier.verifiercomponent.service.api.DataProviderAPIService;

@Service
@AllArgsConstructor
public class VerificationService {
    private final CharacterRecognitionAPIService characterRecognitionAPIService;
    private final DataProviderAPIService dataProviderAPIService;

    public Mono<ResponseEntity<NationalityResponseDTO>> verifyNationality(NationalityVerifyDTO nationalityVerifyDTO) {
        OcrRequestDTO ocrRequestDTO = new OcrRequestDTO();
        ocrRequestDTO.setImage(nationalityVerifyDTO.getEncodedDocument());
        return characterRecognitionAPIService.performRequest(ocrRequestDTO, NationalityResponseDTO.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public Mono<ResponseEntity<StudentResponseDTO>> verifyStudent(StudentVerifyDTO studentVerifyDTO) {
//        OcrRequestDTO ocrRequestDTO = new OcrRequestDTO();
//        ocrRequestDTO.setImage(studentVerifyDTO.getEncodedDocument());
//        return characterRecognitionAPIService.performRequest(ocrRequestDTO, StudentResponseDTO.class)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        //TODO create template for studentUAIC then uncomment previous lines
        //TODO add dataProvider call
        return Mono.just(
                ResponseEntity.ok(
                        StudentResponseDTO.builder()
                                .firstname("Johnny")
                                .lastname("John")
                                .documentIdentification("NUMAR_MATRICOL")
                                .universityName("UAIC")
                                .facultyName("Informatica")
                                .personalIdentification("1234567891234")
                                .build()
                )
        );

    }
}
