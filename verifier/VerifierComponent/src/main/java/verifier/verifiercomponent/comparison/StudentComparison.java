package verifier.verifiercomponent.comparison;

import verifier.verifiercomponent.dto.gateway.StudentVerifyDTO;
import verifier.verifiercomponent.dto.ocr.StudentResponseDTO;
import verifier.verifiercomponent.helpers.ValidationHelper;

public class StudentComparison extends AbstractComparisonStrategy<StudentVerifyDTO, StudentResponseDTO> {

    @Override
    public boolean compare(StudentVerifyDTO userInfo, StudentResponseDTO ocrExtractedInfo) {
        return
            ValidationHelper.isIdValid(ocrExtractedInfo.getPersonalIdentification()) &&
            ValidationHelper.compareStringsStrictCase(userInfo.getPersonalIdentification(), ocrExtractedInfo.getPersonalIdentification()) &&
            ValidationHelper.compareStringsStrictCase(userInfo.getDocumentIdentification(), ocrExtractedInfo.getDocumentIdentification()) &&
            ValidationHelper.compareStringsIgnoreCase(userInfo.getFirstName(), ocrExtractedInfo.getFirstname()) &&
            ValidationHelper.compareStringsIgnoreCase(userInfo.getLastName(), ocrExtractedInfo.getLastname());
            // TODO could add extraction of university and faculty in ocr and validate here
    }
}
