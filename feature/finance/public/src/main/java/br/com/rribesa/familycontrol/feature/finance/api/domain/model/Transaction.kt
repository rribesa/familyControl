package br.com.rribesa.familycontrol.feature.finance.api.domain.model

import java.util.UUID

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val amount: Double,
    val category: String,
    val date: Long,
    val description: String,
    val userId: String
)
