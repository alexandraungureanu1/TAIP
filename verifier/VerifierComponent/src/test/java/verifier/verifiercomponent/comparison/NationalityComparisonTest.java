package verifier.verifiercomponent.comparison;

import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NationalityComparisonTest {

    @Test
    void testCompare_WhenAllFieldsMatch() {
        NationalityVerifyDTO verifyDTO = new NationalityVerifyDTO();
        verifyDTO.setFirstName("John");
        verifyDTO.setLastName("Doe");
        verifyDTO.setCountryCode("RO");
        verifyDTO.setEmail("john.doe@example.com");
        verifyDTO.setPersonalIdentification("1970823123456");
        verifyDTO.setDocumentIdentification("documentType");
        verifyDTO.setEncodedDocument("encodedDataHere");

        NationalityResponseDTO responseDTO = new NationalityResponseDTO();
        responseDTO.setFirstname("John");
        responseDTO.setLastname("Doe");
        responseDTO.setNationality("RO");
        responseDTO.setId("1970823123456");
        responseDTO.setCountry("RO");

        NationalityComparison comparison = new NationalityComparison();
        boolean result = comparison.compare(verifyDTO, responseDTO);
        assertTrue(result);
    }

    @Test
    void testCompare_WhenFieldsDoNotMatch() {
        NationalityVerifyDTO verifyDTO = new NationalityVerifyDTO();
        verifyDTO.setFirstName("John");
        verifyDTO.setLastName("Doe");
        verifyDTO.setCountryCode("RO");
        verifyDTO.setEmail("john.doe36377373@example.com");
        verifyDTO.setPersonalIdentification("1970823123490");
        verifyDTO.setDocumentIdentification("987654321");
        verifyDTO.setEncodedDocument("encodedDataHere");

        NationalityResponseDTO responseDTO = new NationalityResponseDTO();
        responseDTO.setFirstname("John");
        responseDTO.setLastname("Doe");
        responseDTO.setNationality("RO");
        responseDTO.setId("1970823123456");
        responseDTO.setCountry("RO");

        NationalityComparison comparison = new NationalityComparison();
        boolean result = comparison.compare(verifyDTO, responseDTO);
        assertFalse(result);
    }

    @Test
    void testCompare_WhenOneFieldIsNull() {
        NationalityVerifyDTO verifyDTO = new NationalityVerifyDTO();
        // without first & lastname
        verifyDTO.setCountryCode("RO");
        verifyDTO.setEmail("john.doe@example.com");
        verifyDTO.setPersonalIdentification("1970823123457");
        verifyDTO.setDocumentIdentification("987654321");
        verifyDTO.setEncodedDocument("encodedDataHere");

        NationalityResponseDTO responseDTO = new NationalityResponseDTO();
        responseDTO.setFirstname("John");
        responseDTO.setLastname("Doe");
        responseDTO.setNationality("RO");
        responseDTO.setId("1970823123457");
        responseDTO.setCountry("RO");

        NationalityComparison comparison = new NationalityComparison();
        boolean result = comparison.compare(verifyDTO, responseDTO);
        assertFalse(result);
    }

}

