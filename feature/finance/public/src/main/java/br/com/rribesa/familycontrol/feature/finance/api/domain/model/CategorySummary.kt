package br.com.rribesa.familycontrol.feature.finance.api.domain.model

data class CategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Double
)
