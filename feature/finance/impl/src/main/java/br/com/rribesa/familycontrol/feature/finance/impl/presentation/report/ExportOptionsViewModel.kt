package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetMonthlyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException", "SwallowedException", "MagicNumber")
class ExportOptionsViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExportOptionsState())
    val state: StateFlow<ExportOptionsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ExportOptionsEffect>()
    val effect: SharedFlow<ExportOptionsEffect> = _effect.asSharedFlow()

    private var collectJob: Job? = null

    init {
        loadReportStatsForPreview()
    }

    fun onEvent(event: ExportOptionsEvent) {
        when (event) {
            ExportOptionsEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(ExportOptionsEffect.NavigateBack)
                }
            }
            is ExportOptionsEvent.OnPeriodSelected -> {
                _state.update { it.copy(selectedPeriod = event.period) }
                // Optionally reload preview based on selected period (Mês anterior vs atual)
                val cal = Calendar.getInstance()
                if (event.period == "previous") {
                    cal.add(Calendar.MONTH, -1)
                }
                loadReportStatsForPreview(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            }
            is ExportOptionsEvent.OnFormatSelected -> {
                _state.update { it.copy(selectedFormat = event.format) }
            }
            ExportOptionsEvent.OnGenerateClicked -> {
                generateReport()
            }
        }
    }

    private fun loadReportStatsForPreview(
        year: Int = Calendar.getInstance().get(Calendar.YEAR),
        month: Int = Calendar.getInstance().get(Calendar.MONTH)
    ) {
        collectJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        collectJob = viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser().firstOrNull()
                if (user == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageResId = R.string.error_generic
                        )
                    }
                    return@launch
                }
                getMonthlyReportUseCase(user.id, year, month).collect { report ->
                    _state.update {
                        it.copy(
                            monthlyReport = report,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("ExportOptionsViewModel", "Error loading preview stats", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
            }
        }
    }

    private fun generateReport() {
        if (_state.value.isExporting) return

        _state.update { it.copy(isExporting = true, exportSuccess = false) }
        viewModelScope.launch {
            try {
                // Simulate report generation delay (1.5 seconds)
                delay(1500)
                _state.update { it.copy(isExporting = false, exportSuccess = true) }

                // Auto reset success state after 2 seconds
                delay(2000)
                _state.update { it.copy(exportSuccess = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExporting = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
            }
        }
    }
}
