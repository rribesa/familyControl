package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AddTransactionUseCase
import javax.inject.Inject

class AddTransaction @Inject constructor(
    private val repository: TransactionRepository
) : AddTransactionUseCase {
    override suspend fun invoke(transaction: Transaction) {
        repository.addTransaction(transaction)
    }
}
