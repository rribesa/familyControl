package br.com.rribesa.familycontrol.feature.finance.api.domain.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getBudgetStats(): Flow<BudgetStats>
    fun getCategorySummaries(): Flow<List<CategorySummary>>
}
