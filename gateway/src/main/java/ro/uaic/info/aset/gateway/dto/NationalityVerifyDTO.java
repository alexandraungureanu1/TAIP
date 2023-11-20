package ro.uaic.info.aset.gateway.dto;

import lombok.Data;

@Data
public class NationalityVerifyDTO {
    private String firstName;
    private String lastName;
    private String countryCode;
    private String email;
    private String personalIdentification;
    private String documentIdentification;
    private String encodedDocument;
}
