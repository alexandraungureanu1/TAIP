package ro.uaic.info.aset.gateway.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface VerifierApi {

    ResponseEntity<Boolean> verifyAge(HttpServletRequest request);
    ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request);
}
