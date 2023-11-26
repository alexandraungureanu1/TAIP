package verifier.verifiercomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    private final String dataProviderURL = "http://localhost:8083/";
    private final String characterRecognitionURL = "http://localhost:5053"; //TODO
    @Bean
    public WebClient dataProviderAPI(WebClient.Builder builder) {
        return builder.baseUrl(dataProviderURL).build();
    }

    @Bean
    public WebClient characterRecognitionAPI(WebClient.Builder builder) {
        return builder.baseUrl(characterRecognitionURL).build();
    }
}


