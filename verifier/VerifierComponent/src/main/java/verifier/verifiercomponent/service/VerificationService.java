package verifier.verifiercomponent.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.dataprovider.StudentRequestDTO;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityRequestDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;
import verifier.verifiercomponent.service.api.CharacterRecognitionAPIService;
import verifier.verifiercomponent.service.api.DataProviderAPIService;

@Service
@AllArgsConstructor
public class VerificationService {
    private final CharacterRecognitionAPIService characterRecognitionAPIService;
    private final DataProviderAPIService dataProviderAPIService;

    public Mono<ResponseEntity<NationalityResponseDTO>> verifyNationality(NationalityVerifyDTO nationalityVerifyDTO) {
        NationalityRequestDTO nationalityRequest = new NationalityRequestDTO();
//        nationalityRequest.setDocumentIdentification(nationalityVerifyDTO.getDocumentIdentification());
        nationalityRequest.setImage(nationalityVerifyDTO.getEncodedDocument());
        return characterRecognitionAPIService.performRequest(nationalityRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public Mono<ResponseEntity<String>> verifyStudent(StudentVerifyDTO studentVerifyDTO) {
        StudentRequestDTO studentRequest = new StudentRequestDTO(studentVerifyDTO.getUniversityName(),
                studentVerifyDTO.getFacultyName(), studentVerifyDTO.getPersonalIdentification());
        Mono<ResponseEntity<String>> responseEntityMono = dataProviderAPIService.performRequest(studentRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        return responseEntityMono;

    }

    // TODO TO BE DONE with something like strategy (to be done better basically)
    public Boolean compareUserInfoWithDocumentNationality(NationalityVerifyDTO userInfo, NationalityResponseDTO documentInfo) {
        return userInfo.getFirstName().equalsIgnoreCase(documentInfo.getFirstname()) &&
                userInfo.getLastName().equalsIgnoreCase(documentInfo.getLastname()) &&
                documentInfo.getCountrycode().startsWith(userInfo.getCountryCode()); //TODO find a good way to overcome the struggles of the ocr
    }
}
