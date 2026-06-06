package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

interface SyncTransactionsUseCase {
    suspend operator fun invoke(userId: String)
}
