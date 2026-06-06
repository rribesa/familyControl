package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import kotlinx.coroutines.flow.Flow

data class DashboardStats(
    val budgetStats: BudgetStats,
    val categorySummaries: List<CategorySummary>
)

interface GetDashboardStatsUseCase {
    suspend operator fun invoke(): Flow<DashboardStats>
}
