package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginWithGoogleTest {

    private val authRepository: AuthRepository = mockk()
    private val useCase = LoginWithGoogle(authRepository)

    @Test
    fun invoke_delegatesToRepository() = runTest {
        val idToken = "google_token_123"
        val expectedUser = User("1", "google@test.com", "Google User")
        coEvery { authRepository.loginWithGoogle(idToken) } returns expectedUser

        val result = useCase(idToken)

        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { authRepository.loginWithGoogle(idToken) }
    }
}
