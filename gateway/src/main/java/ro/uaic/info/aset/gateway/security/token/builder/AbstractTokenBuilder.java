package ro.uaic.info.aset.gateway.security.token.builder;

import lombok.Setter;
import ro.uaic.info.aset.gateway.security.token.Token;

import java.time.temporal.TemporalAmount;

public abstract class AbstractTokenBuilder {

    @Setter
    protected Token token = new Token();

    public void reset() {
        token = new Token();
    }

    public abstract Token build();
    public abstract AbstractTokenBuilder clientId(String clientId);
    public abstract AbstractTokenBuilder expiryDate(TemporalAmount expireIn);
    public abstract AbstractTokenBuilder sign(String seed);
}
