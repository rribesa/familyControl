package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

sealed interface LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    data object TogglePasswordVisibility : LoginEvent
    data object OnLoginClicked : LoginEvent
    data class OnGoogleLoginClicked(val idToken: String) : LoginEvent
    data object OnForgotPasswordClicked : LoginEvent
    data object OnRegisterClicked : LoginEvent
}

