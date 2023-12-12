package verifier.verifiercomponent.mop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Aspect
public class InvalidValidationAspect {

    @Pointcut("execution(* verifier.verifiercomponent.service.VerificationService.verifyNationality(..))" +
            " && args(nationalityVerifyDTO)")
    public void executeRequest(NationalityVerifyDTO nationalityVerifyDTO) {
    }

    @Before("executeRequest(nationalityVerifyDTO)")
    public void beforeProcessing(NationalityVerifyDTO nationalityVerifyDTO) throws IllegalArgumentException {
        // Check if the image is present and valid
        if (nationalityVerifyDTO.getEncodedDocument() == null &&
                isValidDataUriImage(nationalityVerifyDTO.getEncodedDocument())) {
            log.info("Invalid or missing image in the request");
        }
    }

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
}

