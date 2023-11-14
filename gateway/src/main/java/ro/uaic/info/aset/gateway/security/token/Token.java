package ro.uaic.info.aset.gateway.security.token;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Token {
    private String signature;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String clientId;
    private String issuer;
}
