package verifier.verifiercomponent.comparison;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractComparisonStrategy<T, U> implements ComparisonStrategy<T, U> {

    private static final int[] CONTROL_VALUES = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};

    public boolean isIdValid(String cnp) {
        if (cnp == null || cnp.length() != 13 || !cnp.matches("\\d+")) {
            return false;
        }

        int checksum = 0;
        for (int i = 0; i < 12; i++) {
            checksum += (cnp.charAt(i) - '0') * CONTROL_VALUES[i];
        }
        checksum %= 11;
        if (checksum == 10) {
            checksum = 1;
        }

        return checksum == (cnp.charAt(12) - '0');
    }

    public boolean compareId(String cnp1, String cnp2) {
        if (cnp1 == null || cnp2 == null) {
            return false;
        }
        return cnp1.equals(cnp2);
    }

    public boolean compareName(String name1, String name2) {
        if (name1 == null || name2 == null) {
            return false;
        }
        return name1.equalsIgnoreCase(name2);
    }

    public boolean compareNationality(String nationality1, String nationality2) {
        if (nationality1 == null || nationality2 == null) {
            return false;
        }
        return nationality2.contains(nationality1);
    }
}
