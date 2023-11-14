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
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

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
        System.out.println(TOKEN_EXPIRE_AMOUNT);
        System.out.println(clientsIdList);
        tokenExpiryPeriod = Period.parse(TOKEN_EXPIRE_AMOUNT);
    }

    public Boolean validateToken(String tokenString) {
        Token token = buildTokenFromString(tokenString);
        if(Objects.isNull(token)) {
            return false;
        }

        boolean isTokenValid = true;

        isTokenValid &= "me".equals(token.getIssuer());
        isTokenValid &= (LocalDate.now().isAfter(token.getIssueDate()) || LocalDate.now().isEqual(token.getIssueDate()));
        isTokenValid &= (LocalDate.now().isBefore(token.getExpiryDate()) || LocalDate.now().isEqual(token.getExpiryDate()));
        isTokenValid &= clientsIdList.contains(token.getClientId());

        String signatureToValidate = token.getSignature();
        token.setSignature(null);
        String validSignature = new TokenBuilder(token)
                .sign(SECRET_SEED)
                .build()
                .getSignature();

        isTokenValid &= validSignature.equals(signatureToValidate);

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
                        .sign(SECRET_SEED)
                        .build()
                    )
            );
        } catch (JsonProcessingException e) {
            throw new TokenParsingException(e);
        }
    }

    private Token buildTokenFromString(String tokenString) throws TokenParsingException {
        try {
            String decodedTokenString = Base64Helper.decodeFromBase64(tokenString);
            return objectMapper.readValue(decodedTokenString, Token.class);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new TokenParsingException(e);
        }
    }
}
