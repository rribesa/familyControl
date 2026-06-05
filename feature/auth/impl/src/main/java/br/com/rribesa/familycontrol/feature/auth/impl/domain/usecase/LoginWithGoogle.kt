package br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithGoogleUseCase
import javax.inject.Inject

class LoginWithGoogle @Inject constructor(
    private val authRepository: AuthRepository
) : LoginWithGoogleUseCase {
    override suspend fun invoke(idToken: String): User {
        TODO("Not yet implemented")
    }
}
