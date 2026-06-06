package br.com.rribesa.familycontrol.feature.finance.api.domain.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(userId: String): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun syncTransactions(userId: String)
}
