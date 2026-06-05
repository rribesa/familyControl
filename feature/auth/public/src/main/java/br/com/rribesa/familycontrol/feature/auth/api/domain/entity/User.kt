package br.com.rribesa.familycontrol.feature.auth.api.domain.entity

import java.util.Date

/**
 * Domain entity representing an authenticated user.
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val birthDate: Date?
)
