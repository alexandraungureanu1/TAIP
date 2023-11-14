package ro.uaic.info.aset.gateway.api;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface VerifierAPI {

    ResponseEntity<Boolean> verifyAge(HttpServletRequest request);
    ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request);
}
