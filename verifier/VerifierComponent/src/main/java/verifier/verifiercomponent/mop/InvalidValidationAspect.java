package verifier.verifiercomponent.mop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Aspect
public class InvalidValidationAspect {

    public static boolean isValidDataUriImage(String dataUri) {
        try {
            if (!dataUri.startsWith("data:") || !dataUri.contains(";base64,")) {
                return false;
            }
            String base64Data = dataUri.split(",")[1];
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            return image != null;
        } catch (IllegalArgumentException | IOException e) {
            return false;
        }
    }

    public static String convertAndEncodeImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + encodedImage;
        } catch (IOException e) {
            log.error("Error converting and encoding the image", e);
            return null;
        }
    }

    @Pointcut("execution(* verifier.verifiercomponent.service.VerificationService.verifyNationality(..))" +
            " && args(nationalityVerifyDTO)")
    public void executeRequest(NationalityVerifyDTO nationalityVerifyDTO) {
    }

    @Before("executeRequest(nationalityVerifyDTO)")
    public void beforeProcessing(NationalityVerifyDTO nationalityVerifyDTO) throws IllegalArgumentException {
        if (nationalityVerifyDTO.getEncodedDocument() == null) {
            log.info("No image provided in the request");
            return;
        }

        String encodedImage = nationalityVerifyDTO.getEncodedDocument();
        if (!isValidDataUriImage(encodedImage)) {
            encodedImage = convertAndEncodeImage(encodedImage);
            nationalityVerifyDTO.setEncodedDocument(encodedImage);
        }
    }
}

