package br.com.rribesa.familycontrol.feature.auth.api.domain.usecase

interface ForgotPasswordUseCase {
    suspend operator fun invoke(email: String)
}
