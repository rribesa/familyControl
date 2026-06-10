package br.com.rribesa.familycontrol.feature.auth.api.domain.entity

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String = "Editor"
)
