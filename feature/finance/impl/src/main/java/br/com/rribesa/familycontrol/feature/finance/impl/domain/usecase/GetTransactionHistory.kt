package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetTransactionHistoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionHistory @Inject constructor(
    private val repository: TransactionRepository
) : GetTransactionHistoryUseCase {
    override fun invoke(userId: String): Flow<List<Transaction>> {
        return repository.getTransactions(userId)
    }
}
