package verifier.verifiercomponent.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OcrTemplateResponseDTO {

    private Template template;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Template {
        private String id;
        private String name;
    }
}
