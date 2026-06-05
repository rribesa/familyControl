package br.com.rribesa.familycontrol.feature.auth.api.domain.validation

/**
 * Validator utility to enforce strong password rules.
 * Rules:
 * - 8+ characters
 * - 1 uppercase letter
 * - 1 lowercase letter
 * - 1 number
 * - 1 special character
 */
object PasswordValidator {
    private const val MIN_PASSWORD_LENGTH = 8
    private const val SPECIAL_CHARACTERS = "!@#$%^&*()_+=-[]{}|;:',.<>?~`\"\\"

    fun isValid(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in SPECIAL_CHARACTERS }
    }
}
