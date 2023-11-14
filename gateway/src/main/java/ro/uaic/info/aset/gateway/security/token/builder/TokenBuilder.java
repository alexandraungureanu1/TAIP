package ro.uaic.info.aset.gateway.security.token.builder;

import ro.uaic.info.aset.gateway.security.token.Token;

import java.time.LocalDate;
import java.time.temporal.TemporalAmount;

public class TokenBuilder extends AbstractTokenBuilder {

    public TokenBuilder() {
        super();
    }

    public TokenBuilder(Token token) {
        super();
        this.setToken(token);
    }

    @Override
    public Token build() {
        this.token.setIssuer("me");
        this.token.setIssueDate(LocalDate.now());

        return this.token;
    }

    @Override
    public AbstractTokenBuilder clientId(String clientId) {
        this.token.setClientId(clientId);
        return this;
    }

    @Override
    public AbstractTokenBuilder expiryDate(TemporalAmount expireIn) {
        this.token.setExpiryDate(LocalDate.now().plus(expireIn));
        return this;
    }

    @Override
    public AbstractTokenBuilder sign(String seed) {
        //TODO implement signature
        String signature = "";
        this.token.setSignature(signature);
        return this;
    }
}
