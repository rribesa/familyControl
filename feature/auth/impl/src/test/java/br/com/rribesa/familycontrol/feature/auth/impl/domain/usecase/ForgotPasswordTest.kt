package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ForgotPasswordTest {

    private val authRepository: AuthRepository = mockk()
    private val useCase = ForgotPassword(authRepository)

    @Test
    fun invoke_delegatesToRepository() = runTest {
        val email = "forgot@test.com"
        coEvery { authRepository.sendPasswordResetEmail(email) } returns Unit

        useCase(email)

        coVerify(exactly = 1) { authRepository.sendPasswordResetEmail(email) }
    }
}
