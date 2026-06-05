package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.ForgotPasswordUseCase
import javax.inject.Inject

class ForgotPassword @Inject constructor(
    private val authRepository: AuthRepository
) : ForgotPasswordUseCase {
    override suspend fun invoke(email: String) {
        TODO("Not yet implemented")
    }
}
