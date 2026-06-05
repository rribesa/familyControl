@file:Suppress("TooGenericExceptionCaught", "SwallowedException", "LongMethod")

package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithEmailUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithGoogleUseCase
import br.com.rribesa.familycontrol.core.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        emailErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            is LoginEvent.OnPasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        errorMessageResId = null
                    )
                }
            }
            LoginEvent.TogglePasswordVisibility -> {
                _state.update {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
            LoginEvent.OnLoginClicked -> {
                performLogin()
            }
            LoginEvent.OnGoogleLoginClicked -> {
                performGoogleLogin()
            }
        }
    }

    private fun performLogin() {
        val currentEmail = _state.value.email.trim()
        val currentPassword = _state.value.password

        var hasError = false

        if (currentEmail.isEmpty()) {
            _state.update { it.copy(emailErrorResId = R.string.error_empty_field) }
            hasError = true
        } else if (!emailRegex.matches(currentEmail)) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            hasError = true
        }

        if (currentPassword.isEmpty()) {
            _state.update { it.copy(errorMessageResId = R.string.error_empty_field) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            try {
                loginWithEmailUseCase(currentEmail, currentPassword)
                _effect.emit(LoginEffect.NavigateToDashboard)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(LoginEffect.ShowError(R.string.error_generic))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun performGoogleLogin() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            try {
                // In a real app, we would get the idToken from Google Credential Manager first.
                // For this implementation contract, we stub it with a sample token or trigger delegation.
                loginWithGoogleUseCase(idToken = "stub_google_id_token")
                _effect.emit(LoginEffect.NavigateToDashboard)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(LoginEffect.ShowError(R.string.error_generic))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
