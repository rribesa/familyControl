package br.com.rribesa.familycontrol.feature.finance.api.domain.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(userId: String): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun syncTransactions(userId: String)
    fun getCategories(userId: String): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun syncCategories(userId: String)
}
