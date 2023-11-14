package ro.uaic.info.aset.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ro.uaic.info.aset.gateway.api.VerifierApi;
import ro.uaic.info.aset.gateway.client.VerifierClient;
import ro.uaic.info.aset.gateway.dto.AgeVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("realVerifierController")
public class VerifierController implements VerifierApi {

    private final VerifierClient verifierClient;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Boolean> verifyAge(HttpServletRequest request) {
        AgeVerifyDTO ageVerifyDTO = retrieveDTO(request, AgeVerifyDTO.class);
        return ResponseEntity.ok(verifierClient.verifyAge(ageVerifyDTO));
    }

    @Override
    public ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request) {
        StudentVerifyDTO studentVerifyDTO = retrieveDTO(request, StudentVerifyDTO.class);
        return ResponseEntity.ok(verifierClient.verifyIsStudent(studentVerifyDTO));
    }

    private<T> T retrieveDTO(HttpServletRequest request, Class<T> dtoClass) {
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            return objectMapper.readValue(requestData, dtoClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
