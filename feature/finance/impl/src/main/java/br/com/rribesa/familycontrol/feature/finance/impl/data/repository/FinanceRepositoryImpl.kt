package br.com.rribesa.familycontrol.feature.finance.impl.data.repository

import br.com.rribesa.familycontrol.core.data.FirestorePaths
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("MagicNumber")
class FinanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FinanceRepository {

    override fun getBudgetStats(): Flow<BudgetStats> = callbackFlow {
        val listener = firestore.collection(FirestorePaths.BUDGETS)
            .document("default_budget")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val totalIncome = snapshot.getDouble("totalIncome") ?: 5000.0
                    val totalExpenses = snapshot.getDouble("totalExpenses") ?: 3500.0
                    val budgetLimit = snapshot.getDouble("budgetLimit") ?: 4000.0
                    trySend(BudgetStats(totalIncome, totalExpenses, budgetLimit))
                } else {
                    trySend(BudgetStats(5000.0, 3500.0, 4000.0))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getCategorySummaries(): Flow<List<CategorySummary>> = callbackFlow {
        val listener = firestore.collection(FirestorePaths.CATEGORIES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val summaries = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val amount = doc.getDouble("amount") ?: return@mapNotNull null
                        val percentage = doc.getDouble("percentage") ?: return@mapNotNull null
                        CategorySummary(name, amount, percentage)
                    }
                    trySend(summaries)
                } else {
                    trySend(
                        listOf(
                            CategorySummary("Alimentação", 1200.0, 30.0),
                            CategorySummary("Moradia", 1500.0, 37.5),
                            CategorySummary("Transporte", 500.0, 12.5),
                            CategorySummary("Lazer", 300.0, 7.5)
                        )
                    )
                }
            }
        awaitClose { listener.remove() }
    }
}

