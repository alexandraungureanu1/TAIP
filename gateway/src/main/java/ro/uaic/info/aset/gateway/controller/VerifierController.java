package ro.uaic.info.aset.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ro.uaic.info.aset.gateway.api.VerifierApi;
import ro.uaic.info.aset.gateway.client.VerifierClient;

@RequiredArgsConstructor
@Component("realVerifierController")
public class VerifierController implements VerifierApi {

    private final VerifierClient verifierClient;

    @Override
    public ResponseEntity<Boolean> verifyAge(HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request) {
        return null;
    }

    private<T> T retrieveDTO(HttpServletRequest request, Class<T> dtoClass) {
        return null;
    }
}
