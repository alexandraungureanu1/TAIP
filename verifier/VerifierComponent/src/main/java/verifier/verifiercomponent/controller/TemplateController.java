package verifier.verifiercomponent.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import verifier.verifiercomponent.dto.ocr.OcrTemplateResponseDTO;
import verifier.verifiercomponent.service.TemplateService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("api/templates")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class TemplateController {

    private TemplateService templateService;

    @PostMapping("/id")
    public Mono<ResponseEntity<String>> createTemplate() {
        try {
            ClassPathResource templateResource =
                    new ClassPathResource("static/jsons/TemplateIdOcr.json");
            ClassPathResource imageResource =
                    new ClassPathResource("static/images/valid_template_id.jpg");
            String templatePath = templateResource.getFile().getAbsolutePath();
            String imagePath = imageResource.getFile().getAbsolutePath();

            return templateService.createTemplate(templatePath, imagePath)
                    .flatMap(responseEntity -> {
                        OcrTemplateResponseDTO response = responseEntity.getBody();
                        if (response == null) {
                            return Mono.just(ResponseEntity.ok("Failure in creating template."));
                        }
                        log.info("Template creation status. Templated id: " + response.getTemplate().getId());
                        return Mono.just(ResponseEntity.ok("Template created successfully. Template id: " +
                                response.getTemplate().getId()));
                    })
                    .defaultIfEmpty(ResponseEntity.ok("Failure in creating template."))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("")));
        } catch (IOException e) {
            log.error("Error loading resource files", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure in creating template."));
        }
    }
}
