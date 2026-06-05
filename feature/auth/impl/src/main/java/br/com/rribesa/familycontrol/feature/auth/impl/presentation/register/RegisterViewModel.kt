package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

import android.util.Log
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
            is RegisterEvent.OnFullNameChanged -> updateFullName(event.name)
            is RegisterEvent.OnEmailChanged -> updateEmail(event.email)
            is RegisterEvent.OnPasswordChanged -> updatePassword(event.password)
            is RegisterEvent.OnConfirmPasswordChanged -> updateConfirmPassword(event.password)
            RegisterEvent.TogglePasswordVisibility -> togglePasswordVisibility()
            RegisterEvent.OnRegisterClicked -> performRegistration()
            RegisterEvent.OnGoogleRegisterClicked -> performGoogleRegistration()
        }
    }

    private fun updateFullName(name: String) {
        _state.update {
            it.copy(
                fullName = name,
                fullNameErrorResId = null,
                errorMessageResId = null
            )
        }
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailErrorResId = null,
                errorMessageResId = null
            )
        }
    }



    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordErrorResId = null,
                errorMessageResId = null
            )
        }
    }

    private fun updateConfirmPassword(password: String) {
        _state.update {
            it.copy(
                confirmPassword = password,
                confirmPasswordErrorResId = null,
                errorMessageResId = null
            )
        }
    }

    private fun togglePasswordVisibility() {
        _state.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun validateForm(
        name: String,
        email: String,
        password: String,
        confirm: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            _state.update { it.copy(fullNameErrorResId = R.string.error_empty_field) }
            isValid = false
        }

        if (email.isEmpty()) {
            _state.update { it.copy(emailErrorResId = R.string.error_empty_field) }
            isValid = false
        } else if (!emailRegex.matches(email)) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            isValid = false
        }

        if (!PasswordValidator.isValid(password)) {
            _state.update { it.copy(passwordErrorResId = R.string.error_invalid_password) }
            isValid = false
        }

        if (confirm != password) {
            _state.update { it.copy(confirmPasswordErrorResId = R.string.error_password_mismatch) }
            isValid = false
        }

        return isValid
    }

    private fun performRegistration() {
        val currentName = _state.value.fullName.trim()
        val currentEmail = _state.value.email.trim()
        val currentPassword = _state.value.password
        val currentConfirmPassword = _state.value.confirmPassword

        val isValid = validateForm(
            name = currentName,
            email = currentEmail,
            password = currentPassword,
            confirm = currentConfirmPassword
        )

        if (!isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            kotlin.runCatching {
                registerUserUseCase(
                    email = currentEmail,
                    name = currentName,
                    password = currentPassword
                )
            }.onSuccess {
                _effect.emit(RegisterEffect.NavigateToDashboard)
            }.onFailure { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("RegisterViewModel", "Registration error occurred", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(RegisterEffect.ShowError(R.string.error_generic))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun performGoogleRegistration() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessageResId = null) }
            kotlin.runCatching {
                loginWithGoogleUseCase(idToken = "stub_google_id_token")
            }.onSuccess {
                _effect.emit(RegisterEffect.NavigateToDashboard)
            }.onFailure { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("RegisterViewModel", "Google registration error occurred", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(RegisterEffect.ShowError(R.string.error_generic))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
