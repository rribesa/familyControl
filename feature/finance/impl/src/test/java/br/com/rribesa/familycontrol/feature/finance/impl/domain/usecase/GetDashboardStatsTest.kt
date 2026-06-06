package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDashboardStatsTest {

    private val financeRepository: FinanceRepository = mockk()
    private val useCase = GetDashboardStats(financeRepository)

    @Test
    fun invoke_combinesBudgetStatsAndCategorySummaries() = runTest {
        val stats = BudgetStats(totalIncome = 3000.0, totalExpenses = 1500.0, budgetLimit = 2000.0)
        val summaries = listOf(CategorySummary("Lazer", 500.0, 33.3))

        coEvery { financeRepository.getBudgetStats() } returns flowOf(stats)
        coEvery { financeRepository.getCategorySummaries() } returns flowOf(summaries)

        val result = useCase().first()

        assertEquals(stats, result.budgetStats)
        assertEquals(summaries, result.categorySummaries)
    }
}
