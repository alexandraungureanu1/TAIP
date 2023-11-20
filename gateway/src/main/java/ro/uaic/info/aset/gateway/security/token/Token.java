package ro.uaic.info.aset.gateway.security.token;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

//TODO db for token to check if they were invalidated? (e.g. maybe a client generates a new token, so their old one should be invalidated)
@Data
public class Token {
    private String signature;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String clientId;
    private String issuer;
    private Set<String> roles;
}
