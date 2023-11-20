package ro.uaic.info.aset.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.info.aset.gateway.api.VerifierAPI;
import ro.uaic.info.aset.gateway.client.VerifierClient;
import ro.uaic.info.aset.gateway.dto.NationalityVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

@RequiredArgsConstructor
@RequestMapping("/verify")
@RestController
public class VerifierController implements VerifierAPI {

    private final VerifierClient verifierClient;


    @PostMapping("/nationality")
    @Override
    public ResponseEntity<Boolean> verifyNationality(@RequestBody NationalityVerifyDTO nationalityVerifyDTO) {
        return ResponseEntity.ok(verifierClient.verifyNationality(nationalityVerifyDTO));
    }


    @PostMapping("/student")
    @Override
    public ResponseEntity<Boolean> verifyIsStudent(@RequestBody StudentVerifyDTO studentVerifyDTO) {
        return ResponseEntity.ok(verifierClient.verifyIsStudent(studentVerifyDTO));
    }
}
