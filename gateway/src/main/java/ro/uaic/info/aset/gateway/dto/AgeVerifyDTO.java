package ro.uaic.info.aset.gateway.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AgeVerifyDTO {
    private String name;
    private LocalDate birthDate;
    private Integer ageToBeVerified;
    private String encodedDocument;
}
