package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetTransactionHistoryUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.SyncTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException", "SwallowedException")
class HistoryViewModel @Inject constructor(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val syncTransactionsUseCase: SyncTransactionsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HistoryEffect>()
    val effect: SharedFlow<HistoryEffect> = _effect.asSharedFlow()

    private var collectJob: Job? = null

    init {
        loadHistory()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnFilterChanged -> {
                _state.update {
                    it.copy(
                        filter = event.filter,
                        filteredTransactions = applyFilter(it.transactions, event.filter)
                    )
                }
            }
            HistoryEvent.OnRefresh -> {
                syncRemote()
            }
            HistoryEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(HistoryEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadHistory() {
        collectJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        collectJob = viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser().firstOrNull()
                val userId = user?.id ?: "unknown"

                // Sync first in background
                viewModelScope.launch {
                    try {
                        syncTransactionsUseCase(userId)
                    } catch (e: Exception) {
                        // Ignore sync exceptions on start
                    }
                }

                getTransactionHistoryUseCase(userId).collect { transactions ->
                    _state.update {
                        it.copy(
                            transactions = transactions,
                            filteredTransactions = applyFilter(transactions, it.filter),
                            isLoading = false
                        )
                    }
                }
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

    private fun syncRemote() {
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser().firstOrNull()
                val userId = user?.id ?: "unknown"
                syncTransactionsUseCase(userId)
                _state.update { it.copy(isLoading = false) }
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

    private fun applyFilter(transactions: List<Transaction>, filter: HistoryFilter): List<Transaction> {
        return when (filter) {
            HistoryFilter.ALL -> transactions
            HistoryFilter.INCOME -> transactions.filter { it.category == "Salário" }
            HistoryFilter.EXPENSE -> transactions.filter { it.category != "Salário" }
        }
    }
}
