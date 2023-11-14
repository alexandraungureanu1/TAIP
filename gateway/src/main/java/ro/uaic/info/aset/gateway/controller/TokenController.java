package ro.uaic.info.aset.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.info.aset.gateway.api.TokenAPI;
import ro.uaic.info.aset.gateway.exceptions.InvalidClientIDException;
import ro.uaic.info.aset.gateway.security.token.TokenService;

@RequiredArgsConstructor
@RequestMapping("/token")
@RestController
public class TokenController implements TokenAPI {

    private final TokenService tokenService;

    @GetMapping("/generate")
    @Override
    public ResponseEntity<String> generateAPIToken(@RequestParam String clientId) {
        try {
            return ResponseEntity.ok(tokenService.issueNewToken(clientId));
        } catch (InvalidClientIDException invalidClientIDException) {
            return new ResponseEntity<>("Invalid client ID", HttpStatus.FORBIDDEN);
        }
    }
}
