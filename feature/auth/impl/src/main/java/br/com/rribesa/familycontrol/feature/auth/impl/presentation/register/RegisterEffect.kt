package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

sealed interface RegisterEffect {
    data object NavigateToDashboard : RegisterEffect
    data object NavigateToLogin : RegisterEffect
    data class ShowError(val messageResId: Int) : RegisterEffect
}
