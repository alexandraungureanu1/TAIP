package ro.uaic.info.aset.gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.info.aset.gateway.api.VerifierApi;
import ro.uaic.info.aset.gateway.security.token.TokenService;

@RequiredArgsConstructor
@RequestMapping("/verify")
@RestController
@Slf4j
public class VerifierSecurityProxy implements VerifierApi {

    private final VerifierApi realVerifierController;
    private final TokenService tokenService;

    @PostMapping("/age")
    @Override
    public ResponseEntity<Boolean> verifyAge(HttpServletRequest request) {
        if(Boolean.TRUE.equals(isAuthenticatedRequest(getTokenFromRequest(request)))) {
            return realVerifierController.verifyAge(request);
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @PostMapping("/student")
    @Override
    public ResponseEntity<Boolean> verifyIsStudent(HttpServletRequest request) {
        if(Boolean.TRUE.equals(isAuthenticatedRequest(getTokenFromRequest(request)))) {
            return realVerifierController.verifyIsStudent(request);
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();

    }

    //Authorization header is respect format: "API_KEY <<token>>"
    //TODO verify format is correct
    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization")
                .replace("API_KEY ", "");
    }

    private Boolean isAuthenticatedRequest(String token) {
        return tokenService.validateToken(token);
    }
}
