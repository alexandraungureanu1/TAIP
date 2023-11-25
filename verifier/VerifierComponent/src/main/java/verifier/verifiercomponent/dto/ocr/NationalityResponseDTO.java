package verifier.verifiercomponent.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NationalityResponseDTO {
    private String firstName;
    private String lastName;
    private String countryCode;
    private String personalIdentification;
}
