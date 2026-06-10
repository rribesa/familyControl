package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import kotlinx.coroutines.flow.Flow

interface AggregateExpensesByCategoryUseCase {
    suspend operator fun invoke(userId: String, year: Int, month: Int): Flow<List<CategorySummary>>
}
