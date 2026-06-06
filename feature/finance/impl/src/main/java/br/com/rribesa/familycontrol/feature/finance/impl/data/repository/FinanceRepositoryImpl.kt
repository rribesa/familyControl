package br.com.rribesa.familycontrol.feature.finance.impl.data.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("MagicNumber")
class FinanceRepositoryImpl @Inject constructor() : FinanceRepository {

    override fun getBudgetStats(): Flow<BudgetStats> {
        return flowOf(
            BudgetStats(
                totalIncome = 5000.0,
                totalExpenses = 3500.0,
                budgetLimit = 4000.0
            )
        )
    }

    override fun getCategorySummaries(): Flow<List<CategorySummary>> {
        return flowOf(
            listOf(
                CategorySummary("Alimentação", 1200.0, 30.0),
                CategorySummary("Moradia", 1500.0, 37.5),
                CategorySummary("Transporte", 500.0, 12.5),
                CategorySummary("Lazer", 300.0, 7.5)
            )
        )
    }
}
