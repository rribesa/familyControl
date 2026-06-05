@file:Suppress("TooGenericExceptionCaught", "SwallowedException", "LongMethod")

package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.RegisterUserUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithGoogleUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.validation.PasswordValidator
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
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChanged -> {
                _state.update {
                    it.copy(
                        fullName = event.name,
                        fullNameErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            is RegisterEvent.OnEmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        emailErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            is RegisterEvent.OnBirthDateChanged -> {
                _state.update {
                    it.copy(
                        birthDate = event.date,
                        birthDateErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            is RegisterEvent.OnPasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        passwordErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            is RegisterEvent.OnConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.password,
                        confirmPasswordErrorResId = null,
                        errorMessageResId = null
                    )
                }
            }
            RegisterEvent.TogglePasswordVisibility -> {
                _state.update {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
            RegisterEvent.OnRegisterClicked -> {
                performRegistration()
            }
            RegisterEvent.OnGoogleRegisterClicked -> {
                performGoogleRegistration()
            }
        }
    }

    private fun performRegistration() {
        val currentName = _state.value.fullName.trim()
        val currentEmail = _state.value.email.trim()
        val currentDate = _state.value.birthDate.trim()
        val currentPassword = _state.value.password
        val currentConfirmPassword = _state.value.confirmPassword

        var hasError = false

        if (currentName.isEmpty()) {
            _state.update { it.copy(fullNameErrorResId = R.string.error_empty_field) }
            hasError = true
        }

        if (currentEmail.isEmpty()) {
            _state.update { it.copy(emailErrorResId = R.string.error_empty_field) }
            hasError = true
        } else if (!emailRegex.matches(currentEmail)) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            hasError = true
        }

        if (currentDate.isEmpty()) {
            _state.update { it.copy(birthDateErrorResId = R.string.error_empty_field) }
            hasError = true
        }

        if (!PasswordValidator.isValid(currentPassword)) {
            _state.update { it.copy(passwordErrorResId = R.string.error_invalid_password) }
            hasError = true
        }

        if (currentConfirmPassword != currentPassword) {
            _state.update { it.copy(confirmPasswordErrorResId = R.string.error_password_mismatch) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            try {
                // Ignore birthdate in firebase registration contract for now,
                // since User domain entity holds it but standard register parses
                // email, name, password.
                registerUserUseCase(
                    email = currentEmail,
                    name = currentName,
                    password = currentPassword
                )
                _effect.emit(RegisterEffect.NavigateToDashboard)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(RegisterEffect.ShowError(R.string.error_generic))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun performGoogleRegistration() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            try {
                loginWithGoogleUseCase(idToken = "stub_google_id_token")
                _effect.emit(RegisterEffect.NavigateToDashboard)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(RegisterEffect.ShowError(R.string.error_generic))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
