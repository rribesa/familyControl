package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithEmailUseCase
import javax.inject.Inject

class LoginWithEmail @Inject constructor(
    private val authRepository: AuthRepository
) : LoginWithEmailUseCase {
    override suspend fun invoke(email: String, password: String): User {
        TODO("Not yet implemented")
    }
}
