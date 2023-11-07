package ro.uaic.info.aset.gateway.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudentVerifyDTO {
    private String name;
    private Date birthDate;
    private String universityName;
    private String encodedDocument;
}
