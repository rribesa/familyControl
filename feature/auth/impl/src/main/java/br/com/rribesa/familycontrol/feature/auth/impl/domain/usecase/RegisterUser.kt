package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.RegisterUserUseCase
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val authRepository: AuthRepository
) : RegisterUserUseCase {
    override suspend fun invoke(email: String, name: String, password: String): User {
        TODO("Not yet implemented")
    }
}
