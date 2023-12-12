package verifier.verifiercomponent.comparison;

import verifier.verifiercomponent.dto.gateway.NationalityVerifyDTO;
import verifier.verifiercomponent.dto.ocr.NationalityResponseDTO;

// TODO Extend and improve
// TODO Strategies id validity check based on country
public class NationalityComparison extends AbstractComparisonStrategy<NationalityVerifyDTO, NationalityResponseDTO> {
    @Override
    public boolean compare(NationalityVerifyDTO obj1, NationalityResponseDTO obj2) {
        return super.isIdValid(obj1.getPersonalIdentification()) &&
                super.compareStringsIgnoreCase(obj1.getPersonalIdentification(), obj2.getId()) &&
                super.compareStringsIgnoreCase(obj1.getFirstName(), obj2.getFirstname()) &&
                super.compareStringsIgnoreCase(obj1.getLastName(), obj2.getLastname()) &&
                this.compareNationality(obj1.getCountryCode(), obj2.getNationality());
    }

    private boolean compareNationality(String nationality1, String nationality2) {
        if (nationality1 == null || nationality2 == null) {
            return false;
        }
        return nationality2.contains(nationality1);
    }
}
