package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException")
class DashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect: SharedFlow<DashboardEffect> = _effect.asSharedFlow()

    private var collectJob: Job? = null

    init {
        loadDashboardStats()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.OnRefresh -> {
                loadDashboardStats()
            }
            DashboardEvent.OnReportClicked -> {
                viewModelScope.launch {
                    _effect.emit(DashboardEffect.NavigateToReport)
                }
            }
            DashboardEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    _effect.emit(DashboardEffect.NavigateToRegisterTransaction)
                }
            }
            DashboardEvent.OnViewHistoryClicked -> {
                viewModelScope.launch {
                    _effect.emit(DashboardEffect.NavigateToHistory)
                }
            }
        }
    }

    private fun loadDashboardStats() {
        collectJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        collectJob = viewModelScope.launch {
            try {
                getDashboardStatsUseCase().collect { stats ->
                    _state.update {
                        it.copy(
                            budgetStats = stats.budgetStats,
                            categorySummaries = stats.categorySummaries,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("DashboardViewModel", "Error loading stats", e)
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
