package verifier.verifiercomponent.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.ocr.OcrTemplateResponseDTO;
import verifier.verifiercomponent.service.TemplateService;
import verifier.verifiercomponent.service.api.CharacterRecognitionAPIService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TemplateService.class})
class TemplateIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CharacterRecognitionAPIService characterRecognitionAPIService;

    @BeforeEach
    void setUp() {
        OcrTemplateResponseDTO mockResponse =
                new OcrTemplateResponseDTO(new OcrTemplateResponseDTO.Template("3993", "489494"));
        when(characterRecognitionAPIService.createTemplate(any(), any()))
                .thenReturn(Mono.just(mockResponse));
    }

    @Test
    void testCreateTemplate() {
        webTestClient.post().uri("/api/templates/id")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    assertNotNull(responseBody, "Response body should not be null");
                    assertTrue(responseBody.contains("Template created successfully"),
                            "Response body should contain 'Template created successfully'");
                });
    }
}
