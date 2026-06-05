package br.com.rribesa.familycontrol.feature.auth.api.domain.repository

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface mapping all Auth operations.
 * Concrete implementation will live in the :impl module using Firebase Authentication.
 */
interface AuthRepository {
    /**
     * Gets the currently authenticated user session as a Flow.
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Authenticates a user with email and password.
     */
    suspend fun login(email: String, password: String): User

    /**
     * Authenticates a user using Google Credential Manager (idToken).
     */
    suspend fun loginWithGoogle(idToken: String): User

    /**
     * Registers a new user.
     */
    suspend fun register(email: String, name: String, password: String): User

    /**
     * Signs out the current user.
     */
    suspend fun logout()

    /**
     * Sends a password reset verification email.
     */
    suspend fun sendPasswordResetEmail(email: String)
}
