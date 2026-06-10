package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

interface GetMonthlyReportUseCase {
    suspend operator fun invoke(userId: String, year: Int, month: Int): Flow<MonthlyReport>
}
