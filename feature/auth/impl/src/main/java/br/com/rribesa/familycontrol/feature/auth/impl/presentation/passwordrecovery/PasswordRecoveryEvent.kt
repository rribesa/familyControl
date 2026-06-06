package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

sealed interface PasswordRecoveryEvent {
    data class OnEmailChanged(val email: String) : PasswordRecoveryEvent
    data object OnSendClicked : PasswordRecoveryEvent
    data object OnBackToLoginClicked : PasswordRecoveryEvent
}
