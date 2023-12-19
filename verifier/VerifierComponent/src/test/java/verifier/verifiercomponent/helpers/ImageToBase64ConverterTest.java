package verifier.verifiercomponent.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageToBase64ConverterTest {

    @Test
    void testEncodeImageToBase64Uri_JPG() throws IOException {
        String imagePath = new ClassPathResource("static/images/valid_template_id.jpg").getFile().getAbsolutePath();
        String result = ImageToBase64Converter.encodeImageToBase64Uri(imagePath);
        assertNotNull(result);
        assertTrue(result.startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void testEncodeImageToBase64Uri_PNG() throws IOException {
        String imagePath = new ClassPathResource("test-image.png").getFile().getAbsolutePath();
        String result = ImageToBase64Converter.encodeImageToBase64Uri(imagePath);
        assertNotNull(result);
        assertTrue(result.startsWith("data:image/png;base64,"));
    }

    @Test
    void testEncodeImageToBase64Uri_InvalidPath() {
        String invalidPath = "nonexistent/path/image.jpg";
        assertThrows(IOException.class, () ->
                ImageToBase64Converter.encodeImageToBase64Uri(invalidPath));
    }
}
