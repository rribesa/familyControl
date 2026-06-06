package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary

data class DashboardState(
    val budgetStats: BudgetStats = BudgetStats(0.0, 0.0, 0.0),
    val categorySummaries: List<CategorySummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
