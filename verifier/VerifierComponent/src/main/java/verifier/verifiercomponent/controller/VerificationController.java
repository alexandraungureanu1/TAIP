package verifier.verifiercomponent.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.dataprovider.StudentRequestDTO;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityRequestDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;
import verifier.verifiercomponent.service.VerificationService;

import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@RestController
@RequestMapping("api/verifier")
@AllArgsConstructor
public class VerificationController {
    private static final Logger logger = Logger.getLogger("MyLog");

    private VerificationService verificationService;

    @PostMapping("/nationality")
    public Mono<ResponseEntity<Boolean>> verifyNationality(@RequestBody NationalityVerifyDTO nationalityVerifyDTO) {
        logger.info("Request received for nationality verification");
        return verificationService.verifyNationality(nationalityVerifyDTO)
                .flatMap(responseEntity -> {
                    NationalityResponseDTO body = responseEntity.getBody();
                    if (body == null) {
                        return Mono.just(ResponseEntity.ok(false));
                    }
                    ConsoleHandler consoleHandler = new ConsoleHandler();
                    consoleHandler.setFormatter(new SimpleFormatter());
                    logger.addHandler(consoleHandler);
                    logger.info("Response first name field:" + body.getFirstname());
                    logger.info("Response last name field:" + body.getLastname());
                    logger.info("Response country code field:" + body.getCountrycode());

                    boolean isValid = verificationService.compareUserInfoWithDocumentNationality(nationalityVerifyDTO, body);
                    return Mono.just(ResponseEntity.ok(isValid));
                })
                .defaultIfEmpty(ResponseEntity.ok(false))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)));
    }


    @PostMapping("/student")
    public Mono<ResponseEntity<Boolean>> verifyStudent(@RequestBody StudentVerifyDTO studentVerifyDTO) {
        return verificationService.verifyStudent(studentVerifyDTO)
                .map(responseEntity -> {
                    System.out.println(responseEntity);
                    if (Objects.isNull(responseEntity)) {
                        return ResponseEntity.ok(false);
                    }
                    return ResponseEntity.ok(true);
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)) // Handle empty Mono
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)));
    }

    @PostMapping("/test-nationality")
    public ResponseEntity<?> testNationality(@RequestBody NationalityRequestDTO nationalityRequestDTO) {
        NationalityResponseDTO nationalityResponseDTO = new NationalityResponseDTO("firstName", "lastname", "RO");
        return ResponseEntity.ok(nationalityResponseDTO);
    }

    @PostMapping("/test-student")
    public ResponseEntity<?> testStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.ok(true);
    }
}
