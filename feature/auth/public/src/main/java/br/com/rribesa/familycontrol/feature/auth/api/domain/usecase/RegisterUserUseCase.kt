package br.com.rribesa.familycontrol.feature.auth.api.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User

interface RegisterUserUseCase {
    suspend operator fun invoke(email: String, name: String, password: String): User
}
