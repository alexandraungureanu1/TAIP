package ro.uaic.info.aset.gateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Helper {

    public static String encodeToBase64(String originalString) {
        byte[] bytes = originalString.getBytes(StandardCharsets.UTF_8);
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decodeFromBase64(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8));
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
