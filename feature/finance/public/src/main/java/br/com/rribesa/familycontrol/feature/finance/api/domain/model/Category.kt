package br.com.rribesa.familycontrol.feature.finance.api.domain.model

import java.util.UUID

data class Category(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val userId: String
)
