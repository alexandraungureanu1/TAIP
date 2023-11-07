package ro.uaic.info.aset.gateway.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AgeVerifyDTO {
    private String name;
    private Date birthDate;
    private Integer ageToBeVerified;
    private String encodedDocument;
}
