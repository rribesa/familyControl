package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface GetTransactionHistoryUseCase {
    operator fun invoke(userId: String): Flow<List<Transaction>>
}
