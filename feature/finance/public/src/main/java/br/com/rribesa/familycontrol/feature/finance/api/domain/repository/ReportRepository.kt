package br.com.rribesa.familycontrol.feature.finance.api.domain.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyHistoryBar
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.SpenderSummary
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getMonthlyReport(userId: String, year: Int, month: Int): Flow<MonthlyReport>
    fun getCategorySummaries(userId: String, year: Int, month: Int): Flow<List<CategorySummary>>
    fun getHistoricalExpenses(userId: String, limit: Int = 6): Flow<List<MonthlyHistoryBar>>
    fun getTopSpenders(userId: String, year: Int, month: Int): Flow<List<SpenderSummary>>
}
