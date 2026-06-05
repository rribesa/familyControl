package br.com.rribesa.familycontrol.feature.auth.api.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User

interface LoginWithEmailUseCase {
    suspend operator fun invoke(email: String, password: String): User
}
