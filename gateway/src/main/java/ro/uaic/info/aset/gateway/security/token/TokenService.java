package ro.uaic.info.aset.gateway.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.uaic.info.aset.gateway.security.token.builder.AbstractTokenBuilder;
import ro.uaic.info.aset.gateway.security.token.builder.TokenBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final static String SECRET_SEED = "qwertyasdfgh";
    private final static TemporalAmount TOKEN_EXPIRE_AMOUNT = Duration.ofDays(7);

    private final ObjectMapper objectMapper;

    public Boolean validateToken(String tokenString) {
        Token token = buildTokenFromString(tokenString);
        if(Objects.isNull(token)) {
            return false;
        }

        Boolean isTokenValid = true;

        isTokenValid &= "me".equals(token.getIssuer());
        isTokenValid &= LocalDate.now().isAfter(token.getIssueDate());
        isTokenValid &= LocalDate.now().isBefore(token.getExpiryDate());
        //TODO verify clientId

        String signatureToValidate = token.getSignature();
        token.setSignature(null);
        String validSignature = new TokenBuilder(token)
                .sign(SECRET_SEED)
                .build()
                .getSignature();

        isTokenValid &= validSignature.equals(signatureToValidate);

        return isTokenValid;
    }

    //TODO base64
    public String issueNewToken(String clientId) {
        AbstractTokenBuilder tokenBuilder = new TokenBuilder();
        try {
            return objectMapper.writeValueAsString(
                    tokenBuilder
                    .clientId(clientId)
                    .expiryDate(TOKEN_EXPIRE_AMOUNT)
                    .sign(SECRET_SEED)
                    .build()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Token buildTokenFromString(String tokenString) {
        try {
            //TODO decode base64
            return objectMapper.readValue(tokenString, Token.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
