package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.SyncTransactionsUseCase
import javax.inject.Inject

class SyncTransactions @Inject constructor(
    private val repository: TransactionRepository
) : SyncTransactionsUseCase {
    override suspend fun invoke(userId: String) {
        repository.syncTransactions(userId)
    }
}
