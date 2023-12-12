package verifier.verifiercomponent.comparison;

import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.StudentResponseDTO;

public class StudentComparison extends AbstractComparisonStrategy<StudentVerifyDTO, StudentResponseDTO> {

    @Override
    public boolean compare(StudentVerifyDTO userInfo, StudentResponseDTO ocrExtractedInfo) {
        return
            super.isIdValid(ocrExtractedInfo.getPersonalIdentification()) &&
            super.compareStringsStrictCase(userInfo.getPersonalIdentification(), ocrExtractedInfo.getPersonalIdentification()) &&
            super.compareStringsStrictCase(userInfo.getDocumentIdentification(), ocrExtractedInfo.getDocumentIdentification()) &&
            super.compareStringsIgnoreCase(userInfo.getFirstName(), ocrExtractedInfo.getFirstname()) &&
            super.compareStringsIgnoreCase(userInfo.getLastName(), ocrExtractedInfo.getLastname());
            // TODO could add extraction of university and faculty in ocr and validate here
    }
}
