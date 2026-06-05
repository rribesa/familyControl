package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

data class LoginState(
    val email: String = "",
    val emailErrorResId: Int? = null,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
