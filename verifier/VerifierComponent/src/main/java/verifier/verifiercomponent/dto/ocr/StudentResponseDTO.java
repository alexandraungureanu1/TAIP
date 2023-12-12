package verifier.verifiercomponent.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO {
    private String firstname;
    private String lastname;
    private String documentIdentification;
    private String personalIdentification;
    private String universityName;
    private String facultyName;
}
