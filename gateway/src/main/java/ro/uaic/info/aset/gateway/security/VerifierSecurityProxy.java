package ro.uaic.info.aset.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.info.aset.gateway.api.VerifierAPI;
import ro.uaic.info.aset.gateway.exceptions.TokenParsingException;
import ro.uaic.info.aset.gateway.security.token.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/verify")
@RestController
@Slf4j
public class VerifierSecurityProxy implements VerifierAPI {

    private final VerifierAPI realVerifierController;
    private final TokenService tokenService;

    @PostMapping("/age")
    @Override
    public ResponseEntity<Boolean> verifyAge(HttpServletRequest request) {
        try {
            if (Boolean.TRUE.equals(isAuthenticatedRequest(getTokenFromRequest(request)))) {
                return realVerifierController.verifyAge(request);
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .build();
            }
        } catch (TokenParsingException tokenParsingException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @PostMapping("/student")
    @Override
    public ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request) {
        try {
            if (Boolean.TRUE.equals(isAuthenticatedRequest(getTokenFromRequest(request)))) {
                return realVerifierController.verifyIsStudent(request);
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .build();
            }
        } catch (TokenParsingException tokenParsingException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

    }

    //Authorization header is respect format: "API_KEY <<token>>"
    private String getTokenFromRequest(HttpServletRequest request) throws TokenParsingException{
        String authHeader = request.getHeader("Authorization");
        if(Objects.isNull(authHeader) || !authHeader.startsWith("API_KEY ")) {
            throw new TokenParsingException("Auth header missing or invalid format");
        }

        return authHeader.replace("API_KEY ", "");
    }

    private Boolean isAuthenticatedRequest(String token) {
        try {
            return tokenService.validateToken(token);
        } catch (TokenParsingException tokenParsingException) {
            return false;
        }
    }
}
