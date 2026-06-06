package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

sealed interface PasswordRecoveryEffect {
    data object NavigateToLogin : PasswordRecoveryEffect
    data class ShowError(val messageResId: Int) : PasswordRecoveryEffect
}
