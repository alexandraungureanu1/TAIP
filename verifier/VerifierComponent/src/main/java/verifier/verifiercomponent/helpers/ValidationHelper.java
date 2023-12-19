package verifier.verifiercomponent.helpers;

import java.util.Objects;

public class ValidationHelper {

    private static final int[] CONTROL_VALUES = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};

    public static boolean isIdValid(String cnp) {
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

    public static boolean compareStringsIgnoreCase(String string1, String string2) {
        return  !Objects.isNull(string1) &&
                !Objects.isNull(string2) &&
                string1.equalsIgnoreCase(string2);
    }

    public static boolean compareStringsStrictCase(String string1, String string2) {
        return  !Objects.isNull(string1) &&
                !Objects.isNull(string2) &&
                string1.equalsIgnoreCase(string2);
    }
}
