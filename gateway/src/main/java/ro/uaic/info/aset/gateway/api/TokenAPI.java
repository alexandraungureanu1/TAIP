package ro.uaic.info.aset.gateway.api;

import org.springframework.http.ResponseEntity;

public interface TokenAPI {

    ResponseEntity<String> generateAPIToken(String clientId);
    ResponseEntity<Void> addNewClientId(String clientId);
    ResponseEntity<Void> deleteClientId(String clientId);

}
