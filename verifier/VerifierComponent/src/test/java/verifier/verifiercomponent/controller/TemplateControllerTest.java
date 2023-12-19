package verifier.verifiercomponent.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.ocr.OcrTemplateResponseDTO;
import verifier.verifiercomponent.service.TemplateService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TemplateService templateService;

    @BeforeEach
    void setUp(){
        OcrTemplateResponseDTO mockResponse =
                new OcrTemplateResponseDTO(new OcrTemplateResponseDTO.Template("3993", "489494"));
        when(templateService.createTemplate(anyString(), anyString()))
                .thenReturn(Mono.just(new ResponseEntity<>(mockResponse, HttpStatus.OK)));
    }

    @Test
    void testCreateTemplate() {
        webTestClient.post().uri("/api/templates/id")
                .accept(MediaType.APPLICATION_JSON)
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

