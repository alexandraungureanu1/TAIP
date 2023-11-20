package ro.uaic.info.aset.gateway.api;

import org.springframework.http.ResponseEntity;
import ro.uaic.info.aset.gateway.dto.NationalityVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

public interface VerifierAPI {

    ResponseEntity<Boolean> verifyNationality(NationalityVerifyDTO nationalityVerifyDTO);
    ResponseEntity<Boolean> verifyIsStudent(StudentVerifyDTO studentVerifyDTO);
}
