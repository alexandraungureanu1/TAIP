package verifier.verifiercomponent.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.comparison.ComparisonStrategy;
import verifier.verifiercomponent.comparison.NationalityComparison;
import verifier.verifiercomponent.comparison.StudentComparison;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;
import verifier.verifiercomponent.dto.ocr.StudentResponseDTO;
import verifier.verifiercomponent.mop.OCRServiceMonitoringAspect;
import verifier.verifiercomponent.service.VerificationService;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("api/verifier")
@AllArgsConstructor
public class VerificationController {
    private static final Logger logger = LogManager.getLogger(VerificationController.class);

    private VerificationService verificationService;

    @PostMapping("/nationality")
    public Mono<ResponseEntity<Boolean>> verifyNationality(@RequestBody NationalityVerifyDTO nationalityVerifyDTO) {
        logger.info("Request received for nationality verification");
        return verificationService.verifyNationality(nationalityVerifyDTO)
                .flatMap(responseEntity -> {
                    NationalityResponseDTO nationalityResponseDTO = responseEntity.getBody();
                    if (nationalityResponseDTO == null) {
                        return Mono.just(ResponseEntity.ok(false));
                    }
                    log.info("Response first name field:" + nationalityResponseDTO.getFirstname());
                    log.info("Response last name field:" + nationalityResponseDTO.getLastname());
                    log.info("Response country field:" + nationalityResponseDTO.getCountry());
                    log.info("Response id field:" + nationalityResponseDTO.getId());
                    log.info("Response nationality field:" + nationalityResponseDTO.getNationality());

                    ComparisonStrategy<NationalityVerifyDTO, NationalityResponseDTO> comparisonStrategy =
                            new NationalityComparison();
                    boolean isValid = comparisonStrategy.compare(nationalityVerifyDTO, nationalityResponseDTO);
                    log.info("Is valid: " + isValid);

                    return Mono.just(ResponseEntity.ok(isValid));
                })
                .defaultIfEmpty(ResponseEntity.ok(false))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)));
    }


    @PostMapping("/student")
    public Mono<ResponseEntity<Boolean>> verifyStudent(@RequestBody StudentVerifyDTO studentVerifyDTO) {
        return verificationService.verifyStudent(studentVerifyDTO)
                .flatMap(responseEntity -> {
                    StudentResponseDTO studentResponseDTO = responseEntity.getBody();
                    if (studentResponseDTO == null) {
                        return Mono.just(ResponseEntity.ok(false));
                    }

                    ComparisonStrategy<StudentVerifyDTO, StudentResponseDTO> comparisonStrategy = new StudentComparison();
                    boolean isValid = comparisonStrategy.compare(studentVerifyDTO, studentResponseDTO);
                    log.info("Is valid: " + isValid);

                    return Mono.just(ResponseEntity.ok(isValid));
                })
                .defaultIfEmpty(ResponseEntity.ok(false))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)));
    }
}
