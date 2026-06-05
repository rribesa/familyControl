package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

data class RegisterState(
    val fullName: String = "",
    val fullNameErrorResId: Int? = null,
    val email: String = "",
    val emailErrorResId: Int? = null,
    val birthDate: String = "",
    val birthDateErrorResId: Int? = null,
    val password: String = "",
    val passwordErrorResId: Int? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorResId: Int? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
