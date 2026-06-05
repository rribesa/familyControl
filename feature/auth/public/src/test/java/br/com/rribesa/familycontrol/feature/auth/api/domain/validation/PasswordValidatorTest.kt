package br.com.rribesa.familycontrol.feature.auth.api.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun isValid_withValidPassword_returnsTrue() {
        // 8+ chars, 1 upper, 1 lower, 1 digit, 1 special character
        assertTrue(PasswordValidator.isValid("Strong1#"))
        assertTrue(PasswordValidator.isValid("aB3\$cDefG!"))
        assertTrue(PasswordValidator.isValid("P@ssw0rd2026"))
    }

    @Test
    fun isValid_whenShortPassword_returnsFalse() {
        // Less than 8 chars
        assertFalse(PasswordValidator.isValid("Ab1#"))
        assertFalse(PasswordValidator.isValid("Short1!"))
    }

    @Test
    fun isValid_withoutUppercase_returnsFalse() {
        // No uppercase
        assertFalse(PasswordValidator.isValid("weak123#"))
    }

    @Test
    fun isValid_withoutLowercase_returnsFalse() {
        // No lowercase
        assertFalse(PasswordValidator.isValid("WEAK123#"))
    }

    @Test
    fun isValid_withoutDigit_returnsFalse() {
        // No digit
        assertFalse(PasswordValidator.isValid("NoDigit#"))
    }

    @Test
    fun isValid_withoutSpecialCharacter_returnsFalse() {
        // No special character
        assertFalse(PasswordValidator.isValid("NoSpecial1"))
    }
}
