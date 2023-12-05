package ro.uaic.info.aset.gateway.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ro.uaic.info.aset.gateway.exceptions.TokenParsingException;
import ro.uaic.info.aset.gateway.security.token.Token;
import ro.uaic.info.aset.gateway.security.token.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import static ro.uaic.info.aset.gateway.util.Constants.AUTHORIZATION_HEADER;
import static ro.uaic.info.aset.gateway.util.Constants.TOKEN_HEADER_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class TokenVerifierFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if(Objects.isNull(authorizationHeader)) {
            log.info("Skipping token verification");
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String tokenString = this.getTokenFromRequest(request);
            Token token = tokenService.buildTokenFromString(tokenString);
            Boolean isTokenValid = tokenService.validateToken(token);
            if(Boolean.TRUE.equals(isTokenValid)) {

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        token.getClientId(),
                        null,
                        token.getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet())
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("Invalid token!");
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        } catch (RuntimeException exception) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        filterChain.doFilter(request, response);
    }

    //Authorization header is respect format: "API_KEY <<token>>"
    private String getTokenFromRequest(HttpServletRequest request) throws TokenParsingException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if(Objects.isNull(authHeader) || !authHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new TokenParsingException("Auth header missing or invalid format");
        }

        return authHeader.replace(TOKEN_HEADER_PREFIX, "");
    }
}
