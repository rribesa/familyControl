package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException")
class RegisterTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterTransactionState())
    val state: StateFlow<RegisterTransactionState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterTransactionEffect>()
    val effect: SharedFlow<RegisterTransactionEffect> = _effect.asSharedFlow()

    fun onEvent(event: RegisterTransactionEvent) {
        when (event) {
            is RegisterTransactionEvent.OnAmountChanged -> {
                _state.update { it.copy(amount = event.amount, errorMessageResId = null) }
            }
            is RegisterTransactionEvent.OnCategoryChanged -> {
                _state.update { it.copy(category = event.category, errorMessageResId = null) }
            }
            is RegisterTransactionEvent.OnDescriptionChanged -> {
                _state.update { it.copy(description = event.description, errorMessageResId = null) }
            }
            is RegisterTransactionEvent.OnDateChanged -> {
                _state.update { it.copy(date = event.date) }
            }
            RegisterTransactionEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(RegisterTransactionEffect.NavigateBack)
                }
            }
            RegisterTransactionEvent.OnSaveClicked -> {
                saveTransaction()
            }
        }
    }

    private fun saveTransaction() {
        val currentAmountStr = _state.value.amount
        val currentCategory = _state.value.category
        val currentDesc = _state.value.description
        val currentDate = _state.value.date

        val amount = currentAmountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _state.update { it.copy(errorMessageResId = R.string.transactions_error_amount) }
            return
        }

        if (currentCategory.isBlank()) {
            _state.update { it.copy(errorMessageResId = R.string.transactions_error_category) }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessageResId = null) }

        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser().firstOrNull()
                val userId = user?.id ?: "unknown"

                val transaction = Transaction(
                    id = UUID.randomUUID(),
                    amount = amount,
                    category = currentCategory,
                    date = currentDate,
                    description = currentDesc,
                    userId = userId
                )

                addTransactionUseCase(transaction)

                _state.update { it.copy(isLoading = false, success = true) }
                _effect.emit(RegisterTransactionEffect.ShowSuccessMessage)
                _effect.emit(RegisterTransactionEffect.NavigateBack)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
            }
        }
    }
}
