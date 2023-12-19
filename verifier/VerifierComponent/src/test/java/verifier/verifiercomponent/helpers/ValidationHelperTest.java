package verifier.verifiercomponent.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationHelperTest {

    @Test
    void testIsIdValid_WithValidCNP() {
        String validCNP = "1970823123456"; // valid cnp
        assertTrue(ValidationHelper.isIdValid(validCNP));
    }

    @Test
    void testIsIdValid_WithInvalidCNP() {
        String invalidCNP = "1970823123457"; // invalid CNP
        assertFalse(ValidationHelper.isIdValid(invalidCNP));
    }

    @Test
    void testCompareStringsIgnoreCase_WithEqualStrings() {
        String string1 = "TestString";
        String string2 = "teststring";
        assertTrue(ValidationHelper.compareStringsIgnoreCase(string1, string2));
    }

    @Test
    void testCompareStringsIgnoreCase_WithUnequalStrings() {
        String string1 = "TestString";
        String string2 = "AnotherString";
        assertFalse(ValidationHelper.compareStringsIgnoreCase(string1, string2));
    }

    @Test
    void testCompareStringsIgnoreCase_WithNullValues() {
        String string1 = null;
        String string2 = "teststring";
        assertFalse(ValidationHelper.compareStringsIgnoreCase(string1, string2));
    }

    @Test
    void testCompareStringsStrictCase_WithEqualStrings() {
        String string1 = "TestString";
        String string2 = "TestString";
        assertTrue(ValidationHelper.compareStringsStrictCase(string1, string2));
    }
}

