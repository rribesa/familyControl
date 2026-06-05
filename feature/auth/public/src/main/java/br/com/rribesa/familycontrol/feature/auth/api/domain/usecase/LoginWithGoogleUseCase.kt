package br.com.rribesa.familycontrol.feature.auth.api.domain.usecase

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): User
}
