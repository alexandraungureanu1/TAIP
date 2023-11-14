package ro.uaic.info.aset.gateway.api;

import org.springframework.http.ResponseEntity;

public interface TokenAPI {

    ResponseEntity<String> generateAPIToken(String clientId);
}
