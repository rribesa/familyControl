package br.com.rribesa.familycontrol.feature.auth.api.domain.repository

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>

    suspend fun login(email: String, password: String): User

    suspend fun loginWithGoogle(idToken: String): User

    suspend fun register(email: String, name: String, password: String): User

    suspend fun logout()

    suspend fun sendPasswordResetEmail(email: String)
}
