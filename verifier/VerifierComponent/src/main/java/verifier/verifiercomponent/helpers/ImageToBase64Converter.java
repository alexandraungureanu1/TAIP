package verifier.verifiercomponent.helpers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageToBase64Converter {

    public static String encodeImageToBase64Uri(String imagePath) throws IOException {
        String formatName = getFormatNameFromPath(imagePath);

        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(image, formatName, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        return "data:image/" + formatName + ";base64," + base64Image;
    }

    private static String getFormatNameFromPath(String imagePath) {
        String extension = imagePath.substring(imagePath.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "jpeg";
            case "png":
                return "png";
            default:
                return "unknown";
        }
    }
}
