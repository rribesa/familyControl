package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.DashboardStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDashboardStats @Inject constructor(
    private val financeRepository: FinanceRepository
) : GetDashboardStatsUseCase {
    override suspend fun invoke(): Flow<DashboardStats> {
        return combine(
            financeRepository.getBudgetStats(),
            financeRepository.getCategorySummaries()
        ) { stats, summaries ->
            DashboardStats(stats, summaries)
        }
    }
}
