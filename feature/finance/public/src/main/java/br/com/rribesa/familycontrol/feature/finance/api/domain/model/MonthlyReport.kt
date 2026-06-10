package br.com.rribesa.familycontrol.feature.finance.api.domain.model

data class MonthlyReport(
    val monthName: String, // e.g. "Outubro 2023"
    val year: Int,
    val month: Int, // 0-11
    val totalExpenses: Double,
    val budgetLimit: Double,
    val remainingBudget: Double,
    val percentUsed: Double,
    val limitStatus: String, // "within", "warning", "critical"
    val history6Months: List<MonthlyHistoryBar>,
    val categoryBreakdown: List<CategorySummary>,
    val topSpenders: List<SpenderSummary>
)

data class MonthlyHistoryBar(
    val monthLabel: String, // e.g. "MAI", "JUN"
    val amount: Double,
    val percentageOfMax: Double // 0 to 100
)

data class SpenderSummary(
    val userId: String,
    val name: String,
    val totalSpent: Double,
    val rank: Int,
    val role: String,
    val photoUrl: String? = null
)
