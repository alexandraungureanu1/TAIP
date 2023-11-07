package ro.uaic.info.aset.gateway.security.token;

import org.springframework.stereotype.Component;

@Component
public class TokenService {

    public Boolean validateToken(String token) {
        return null;
    }

    public Boolean issueNewToken(String clientId) {
        return null;
    }

    private Token buildTokenFromString(String token) {
        return null;
    }
}
