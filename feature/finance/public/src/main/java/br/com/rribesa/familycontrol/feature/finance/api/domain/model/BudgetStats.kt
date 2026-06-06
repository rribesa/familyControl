package br.com.rribesa.familycontrol.feature.finance.api.domain.model

enum class HealthStatus {
    HEALTHY,  // Blue (< 80%)
    WARNING,  // Yellow (80% - 120%)
    CRITICAL  // Red (> 120%)
}

data class BudgetStats(
    val totalIncome: Double,
    val totalExpenses: Double,
    val budgetLimit: Double,
    val currency: String = "R$"
) {
    val balance: Double get() = totalIncome - totalExpenses
    val expensePercentage: Double
        get() = if (budgetLimit > 0) (totalExpenses / budgetLimit) * PERCENT_MULTIPLIER else 0.0

    val healthStatus: HealthStatus get() = when {
        expensePercentage < THRESHOLD_HEALTHY -> HealthStatus.HEALTHY
        expensePercentage <= THRESHOLD_CRITICAL -> HealthStatus.WARNING
        else -> HealthStatus.CRITICAL
    }

    companion object {
        private const val PERCENT_MULTIPLIER = 100.0
        private const val THRESHOLD_HEALTHY = 80.0
        private const val THRESHOLD_CRITICAL = 120.0
    }
}

