package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

sealed interface RegisterEvent {
    data class OnFullNameChanged(val name: String) : RegisterEvent
    data class OnEmailChanged(val email: String) : RegisterEvent
    data class OnBirthDateChanged(val date: String) : RegisterEvent
    data class OnPasswordChanged(val password: String) : RegisterEvent
    data class OnConfirmPasswordChanged(val password: String) : RegisterEvent
    data object TogglePasswordVisibility : RegisterEvent
    data object OnRegisterClicked : RegisterEvent
    data object OnGoogleRegisterClicked : RegisterEvent
}
