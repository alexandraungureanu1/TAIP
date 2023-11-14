package ro.uaic.info.aset.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ro.uaic.info.aset.gateway.api.VerifierAPI;
import ro.uaic.info.aset.gateway.client.VerifierClient;
import ro.uaic.info.aset.gateway.dto.AgeVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;
import ro.uaic.info.aset.gateway.exceptions.DTOParsingException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component("realVerifierController")
public class VerifierController implements VerifierAPI {

    private final VerifierClient verifierClient;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Boolean> verifyAge(HttpServletRequest request) {
        try {
            AgeVerifyDTO ageVerifyDTO = retrieveDTO(request, AgeVerifyDTO.class);
            return ResponseEntity.ok(true);//verifierClient.verifyAge(ageVerifyDTO));
        } catch (DTOParsingException dtoParsingException) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request) {
        try {
            StudentVerifyDTO studentVerifyDTO = retrieveDTO(request, StudentVerifyDTO.class);
            return ResponseEntity.ok(true);//verifierClient.verifyIsStudent(studentVerifyDTO));
        } catch (DTOParsingException dtoParsingException) {
            return ResponseEntity.badRequest().build();
        }
    }

    private<T> T retrieveDTO(HttpServletRequest request, Class<T> dtoClass) throws DTOParsingException{
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            return objectMapper.readValue(requestData, dtoClass);
        } catch (IOException e) {
            throw new DTOParsingException(e);
        }
    }
}
