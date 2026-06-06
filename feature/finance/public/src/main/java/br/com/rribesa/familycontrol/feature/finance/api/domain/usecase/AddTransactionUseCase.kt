package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction

interface AddTransactionUseCase {
    suspend operator fun invoke(transaction: Transaction)
}
