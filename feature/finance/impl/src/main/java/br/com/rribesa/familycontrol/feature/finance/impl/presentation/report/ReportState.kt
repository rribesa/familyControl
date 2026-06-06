package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary

data class ReportState(
    val categorySummaries: List<CategorySummary> = emptyList(),
    val totalExpenses: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
