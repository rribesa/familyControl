package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport

data class ExportOptionsState(
    val monthlyReport: MonthlyReport? = null,
    val selectedPeriod: String = "current", // "current", "previous", "custom"
    val selectedFormat: String = "pdf", // "pdf", "excel", "csv"
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
