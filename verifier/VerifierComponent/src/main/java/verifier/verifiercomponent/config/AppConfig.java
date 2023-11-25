package verifier.verifiercomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    private final String dataProviderURL = "http://localhost:8080/api/verifier";
    private final String characterRecognitionURL = "http://localhost:8080/api/verifier";
    @Bean
    public WebClient dataProviderAPI(WebClient.Builder builder) {
        return builder.baseUrl(dataProviderURL).build();
    }

    @Bean
    public WebClient characterRecognitionAPI(WebClient.Builder builder) {
        return builder.baseUrl(characterRecognitionURL).build();
    }
}


