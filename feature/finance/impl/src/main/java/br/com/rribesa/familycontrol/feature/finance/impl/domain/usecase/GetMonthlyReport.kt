package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.ReportRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetMonthlyReportUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyReport @Inject constructor(
    private val reportRepository: ReportRepository
) : GetMonthlyReportUseCase {
    override suspend fun invoke(userId: String, year: Int, month: Int): Flow<MonthlyReport> {
        return reportRepository.getMonthlyReport(userId, year, month)
    }
}
