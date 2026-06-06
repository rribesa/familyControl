package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

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
class ReportViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ReportEffect>()
    val effect: SharedFlow<ReportEffect> = _effect.asSharedFlow()

    private var collectJob: Job? = null

    init {
        loadReportStats()
    }

    fun onEvent(event: ReportEvent) {
        when (event) {
            ReportEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(ReportEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadReportStats() {
        collectJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        collectJob = viewModelScope.launch {
            try {
                getDashboardStatsUseCase().collect { stats ->
                    _state.update {
                        it.copy(
                            categorySummaries = stats.categorySummaries,
                            totalExpenses = stats.budgetStats.totalExpenses,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("ReportViewModel", "Error loading report stats", e)
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
