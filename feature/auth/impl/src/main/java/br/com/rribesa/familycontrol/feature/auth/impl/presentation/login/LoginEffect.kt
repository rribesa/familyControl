package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

sealed interface LoginEffect {
    data object NavigateToDashboard : LoginEffect
    data object NavigateToRegister : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data class ShowError(val messageResId: Int) : LoginEffect
}
