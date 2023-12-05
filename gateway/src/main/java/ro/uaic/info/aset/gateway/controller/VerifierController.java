package ro.uaic.info.aset.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.aset.gateway.api.VerifierAPI;
import ro.uaic.info.aset.gateway.client.VerifierClient;
import ro.uaic.info.aset.gateway.dto.NationalityVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/verify")
@RestController
public class VerifierController implements VerifierAPI {

    private final VerifierClient verifierClient;


    @PostMapping("/nationality")
    @Override
    public ResponseEntity<Boolean> verifyNationality(@RequestBody NationalityVerifyDTO nationalityVerifyDTO) {
        log.info("Request received for nationality verification");
        return ResponseEntity.ok(verifierClient.verifyNationality(nationalityVerifyDTO));
    }


    @PostMapping("/student")
    @Override
    public ResponseEntity<Boolean> verifyIsStudent(@RequestBody StudentVerifyDTO studentVerifyDTO) {
        return ResponseEntity.ok(verifierClient.verifyIsStudent(studentVerifyDTO));
    }
}
