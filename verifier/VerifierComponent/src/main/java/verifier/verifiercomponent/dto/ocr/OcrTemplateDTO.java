package verifier.verifiercomponent.dto.ocr;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OcrTemplateDTO {
    private String id;
    private String name;
    private List<Page> pages;

    @Setter
    @Getter
    public static class Page {
        private String id;
        private String name;
        private String image;
        private List<Field> fields;

        @Setter
        @Getter
        public static class Field {
            private String id;
            private String name;
            private String page_id;
            private boolean sensitive;
            private String origin_set;
            private String category;
            private int p1;
            private int p2;
            private int p3;
            private int p4;
        }
    }
}

