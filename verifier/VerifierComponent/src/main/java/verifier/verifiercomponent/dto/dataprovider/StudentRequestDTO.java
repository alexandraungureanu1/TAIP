package verifier.verifiercomponent.dto.dataprovider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO {
    private String universityName;
    private String facultyName;
    private String personalIdentification;
}
