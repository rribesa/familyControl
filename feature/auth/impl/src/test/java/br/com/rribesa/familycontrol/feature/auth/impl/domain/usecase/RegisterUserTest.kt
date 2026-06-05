package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class RegisterUserTest {

    private val authRepository: AuthRepository = mockk()
    private val useCase = RegisterUser(authRepository)

    @Test
    fun invoke_delegatesToRepository() = runTest {
        val email = "new@test.com"
        val name = "New User"
        val password = "Strong1#"
        val expectedUser = User("2", email, name, Date(0))
        coEvery { authRepository.register(email, name, password) } returns expectedUser

        val result = useCase(email, name, password)

        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { authRepository.register(email, name, password) }
    }
}
