package ro.uaic.info.aset.gateway.security.token.builder;

import ro.uaic.info.aset.gateway.exceptions.InvalidClientIDException;
import ro.uaic.info.aset.gateway.security.token.Token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ro.uaic.info.aset.gateway.util.Constants.ISSUER;

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
        this.token.setIssuer(ISSUER);
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
    public AbstractTokenBuilder sign(String signature) {
        this.token.setSignature(signature);
        return this;
    }

    @Override
    public AbstractTokenBuilder roles(Set<String> roles) {
        Set<String> rolesSet = new HashSet<>(roles);
        this.token.setRoles(rolesSet);
        return this;
    }

    @Override
    public AbstractTokenBuilder role(String role) {
        this.token.getRoles().add(role);
        return this;
    }
}
