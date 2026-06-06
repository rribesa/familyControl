package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.ForgotPasswordUseCase
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
class PasswordRecoveryViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordRecoveryState())
    val state: StateFlow<PasswordRecoveryState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PasswordRecoveryEffect>()
    val effect: SharedFlow<PasswordRecoveryEffect> = _effect.asSharedFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun onEvent(event: PasswordRecoveryEvent) {
        when (event) {
            is PasswordRecoveryEvent.OnEmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        emailErrorResId = null,
                        errorMessageResId = null,
                        isSuccess = false
                    )
                }
            }
            PasswordRecoveryEvent.OnSendClicked -> {
                performPasswordRecovery()
            }
            PasswordRecoveryEvent.OnBackToLoginClicked -> {
                viewModelScope.launch {
                    _effect.emit(PasswordRecoveryEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun performPasswordRecovery() {
        val currentEmail = _state.value.email.trim()

        if (currentEmail.isEmpty()) {
            _state.update { it.copy(emailErrorResId = R.string.error_empty_field) }
            return
        }

        if (!emailRegex.matches(currentEmail)) {
            _state.update { it.copy(emailErrorResId = R.string.error_invalid_email) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    emailErrorResId = null,
                    errorMessageResId = null,
                    isSuccess = false
                )
            }
            kotlin.runCatching {
                forgotPasswordUseCase(currentEmail)
            }.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }.onFailure { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("PasswordRecoveryVM", "Password recovery error occurred", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
                _effect.emit(PasswordRecoveryEffect.ShowError(R.string.error_generic))
            }
        }
    }
}
