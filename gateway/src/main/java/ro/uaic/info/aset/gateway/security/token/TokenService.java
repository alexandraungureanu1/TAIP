package ro.uaic.info.aset.gateway.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.uaic.info.aset.gateway.exceptions.InvalidClientIDException;
import ro.uaic.info.aset.gateway.exceptions.TokenParsingException;
import ro.uaic.info.aset.gateway.security.token.builder.AbstractTokenBuilder;
import ro.uaic.info.aset.gateway.security.token.builder.TokenBuilder;
import ro.uaic.info.aset.gateway.util.Base64Helper;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ro.uaic.info.aset.gateway.util.Constants.ADMIN_CLIENT_ID;
import static ro.uaic.info.aset.gateway.util.Constants.ISSUER;

@Component
@RequiredArgsConstructor
public class TokenService {
    private final static String SECRET_SEED = "qwertyasdfgh";

    @Value("${token.expiry-amount}")
    private String TOKEN_EXPIRE_AMOUNT;

    @Value("#{'${token.clients}'.split(',')}")
    private List<String> clientsIdList; //TODO to be moved in a better storage
    private Period tokenExpiryPeriod;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        tokenExpiryPeriod = Period.parse(TOKEN_EXPIRE_AMOUNT);
    }

    public Boolean validateToken(Token token) {
        if(Objects.isNull(token)) {
            return false;
        }

        boolean isTokenValid = true;

        isTokenValid &= ISSUER.equals(token.getIssuer());
        isTokenValid &= (LocalDate.now().isAfter(token.getIssueDate()) || LocalDate.now().isEqual(token.getIssueDate()));
        isTokenValid &= (LocalDate.now().isBefore(token.getExpiryDate()) || LocalDate.now().isEqual(token.getExpiryDate()));
        isTokenValid &= clientsIdList.contains(token.getClientId());
        isTokenValid &= this.validateSignature(token);

        return isTokenValid;
    }

    public String issueNewToken(String clientId) throws InvalidClientIDException, TokenParsingException {
        AbstractTokenBuilder tokenBuilder = new TokenBuilder();
        if(!clientsIdList.contains(clientId)) {
            throw new InvalidClientIDException();
        }
        try {

            return Base64Helper.encodeToBase64(
                    objectMapper.writeValueAsString(
                        tokenBuilder
                        .clientId(clientId)
                        .expiryDate(tokenExpiryPeriod)
                        .roles(getRolesForClientId(clientId))
                        .sign(createTokenSignature(clientId))
                        .build()
                    )
            );
        } catch (JsonProcessingException e) {
            throw new TokenParsingException(e);
        }
    }

    public Token buildTokenFromString(String tokenString) throws TokenParsingException {
        try {
            String decodedTokenString = Base64Helper.decodeFromBase64(tokenString);
            return objectMapper.readValue(decodedTokenString, Token.class);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new TokenParsingException(e);
        }
    }

    private Set<String> getRolesForClientId(String clientId) {
        Set<String> roles = new HashSet<>();

        roles.add("ROLE_BASIC");
        if(ADMIN_CLIENT_ID.equals(clientId)) {
             roles.add("ROLE_ADMIN");
        }

        return roles;
    }

    private String createTokenSignature(String clientId) {
        if(Objects.isNull(clientId))
            throw new InvalidClientIDException();

        String tokenEncryptionString = String.format("%s.%s.%s", ISSUER, clientId, SECRET_SEED);
        return encryptString(tokenEncryptionString);
    }

    private boolean validateSignature(Token token) {
        String signatureToValidate = token.getSignature();
        boolean isTokenValid;
        try {
            isTokenValid = createTokenSignature(token.getClientId()).equals(signatureToValidate);
        } catch (RuntimeException runtimeException) {
            isTokenValid = false;
        }
        return isTokenValid;
    }

    private String encryptString(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");
            messageDigest.update(input.getBytes());
            byte[] digestBytes = messageDigest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digestBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // TODO this should be handled better...
        }
    }
}
