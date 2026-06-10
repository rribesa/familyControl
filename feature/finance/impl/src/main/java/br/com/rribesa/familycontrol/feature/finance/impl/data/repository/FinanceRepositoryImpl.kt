package br.com.rribesa.familycontrol.feature.finance.impl.data.repository

import br.com.rribesa.familycontrol.core.data.FirestorePaths
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.TransactionDao
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.CategoryDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("MagicNumber")
class FinanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val authRepository: AuthRepository
) : FinanceRepository {

    private fun getBudgetLimit(): Flow<Double> = callbackFlow {
        val listener = firestore.collection(FirestorePaths.BUDGETS)
            .document("default_budget")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(4000.0)
                    return@addSnapshotListener
                }
                val limit = snapshot?.getDouble("budgetLimit") ?: 4000.0
                trySend(limit)
            }
        awaitClose { listener.remove() }
    }

    override fun getBudgetStats(): Flow<BudgetStats> {
        return authRepository.getCurrentUser().flatMapLatest { user ->
            if (user == null) {
                flowOf(BudgetStats(0.0, 0.0, 4000.0))
            } else {
                combine(
                    transactionDao.getTransactions(user.id),
                    getBudgetLimit()
                ) { entities, limit ->
                    val totalExpenses = entities.sumOf { it.amount }
                    BudgetStats(
                        totalIncome = 0.0,
                        totalExpenses = totalExpenses,
                        budgetLimit = limit
                    )
                }
            }
        }
    }

    override fun getCategorySummaries(): Flow<List<CategorySummary>> {
        return authRepository.getCurrentUser().flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                combine(
                    transactionDao.getTransactions(user.id),
                    categoryDao.getCategories(user.id)
                ) { entities, categoryEntities ->
                    val totalExpenses = entities.sumOf { it.amount }
                    val categories = if (categoryEntities.isEmpty()) {
                        listOf("Alimentação", "Moradia", "Transporte", "Lazer", "Outros")
                    } else {
                        categoryEntities.map { it.name }
                    }
                    categories.map { cat ->
                        val amount = entities.filter { it.category == cat }.sumOf { it.amount }
                        val percentage = if (totalExpenses > 0) (amount / totalExpenses) * 100.0 else 0.0
                        CategorySummary(
                            category = cat,
                            amount = amount,
                            percentage = percentage
                        )
                    }
                }
            }
        }
    }
}

