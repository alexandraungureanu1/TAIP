package verifier.verifiercomponent.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NationalityResponseDTO {
    private String firstname;
    private String lastname;
    private String countrycode;
}
