package ro.uaic.info.aset.gateway.security.token.builder;

import ro.uaic.info.aset.gateway.security.token.Token;

import java.util.Date;

public abstract class AbstractTokenBuilder {

    private Token token = new Token();

    public void reset() {}

    public abstract Token build();
    public abstract AbstractTokenBuilder clientId(String clientId);
    public abstract AbstractTokenBuilder expiryDate(Date expiryDate);
    public abstract AbstractTokenBuilder sign(String seed);
}
