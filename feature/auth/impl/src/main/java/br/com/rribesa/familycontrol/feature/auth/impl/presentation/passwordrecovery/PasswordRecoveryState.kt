package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

data class PasswordRecoveryState(
    val email: String = "",
    val emailErrorResId: Int? = null,
    val errorMessageResId: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
