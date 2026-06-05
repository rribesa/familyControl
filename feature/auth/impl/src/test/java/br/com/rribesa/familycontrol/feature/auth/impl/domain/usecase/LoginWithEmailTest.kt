package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginWithEmailTest {

    private val authRepository: AuthRepository = mockk()
    private val useCase = LoginWithEmail(authRepository)

    @Test
    fun invoke_delegatesToRepository() = runTest {
        val email = "user@test.com"
        val password = "Strong1#"
        val expectedUser = User("1", email, "Test User")
        coEvery { authRepository.login(email, password) } returns expectedUser

        val result = useCase(email, password)

        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { authRepository.login(email, password) }
    }
}
