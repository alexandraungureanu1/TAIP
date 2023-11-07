package ro.uaic.info.aset.gateway.security.token;

import lombok.Data;

import java.util.Date;

@Data
public class Token {
    private String signature;
    private Date issueDate;
    private Date expiryDate;
    private String clientId;
}
