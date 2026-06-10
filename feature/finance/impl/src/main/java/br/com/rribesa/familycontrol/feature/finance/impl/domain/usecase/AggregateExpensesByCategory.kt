package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.ReportRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AggregateExpensesByCategoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AggregateExpensesByCategory @Inject constructor(
    private val reportRepository: ReportRepository
) : AggregateExpensesByCategoryUseCase {
    override suspend fun invoke(userId: String, year: Int, month: Int): Flow<List<CategorySummary>> {
        return reportRepository.getCategorySummaries(userId, year, month)
    }
}
