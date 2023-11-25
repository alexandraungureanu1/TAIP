package verifier.verifiercomponent.dto.gateway;

import lombok.Data;

@Data
public class StudentVerifyDTO {
    private String firstName;
    private String lastName;
    private String universityName;
    private String facultyName;
    private String email;
    private String personalIdentification;
    private String documentIdentification;
    private String encodedDocument;
}
