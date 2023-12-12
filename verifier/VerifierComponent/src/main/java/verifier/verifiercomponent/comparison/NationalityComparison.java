package verifier.verifiercomponent.comparison;

import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;

// TODO Extend and improve
// TODO Strategies id validity check based on country
public class NationalityComparison extends AbstractComparisonStrategy<NationalityVerifyDTO, NationalityResponseDTO> {
    @Override
    public boolean compare(NationalityVerifyDTO obj1, NationalityResponseDTO obj2) {
        return super.isIdValid(obj1.getPersonalIdentification()) &&
                super.compareId(obj1.getPersonalIdentification(), obj2.getId()) &&
                super.compareName(obj1.getFirstName(), obj2.getFirstname()) &&
                super.compareName(obj1.getLastName(), obj2.getLastname()) &&
                super.compareNationality(obj1.getCountryCode(), obj2.getNationality());
    }
}
