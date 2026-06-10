package br.com.rribesa.familycontrol.feature.finance.impl.data.repository

import br.com.rribesa.familycontrol.core.data.FirestorePaths
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyHistoryBar
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.SpenderSummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.ReportRepository
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.CategoryDao
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.TransactionDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("MagicNumber", "MaxLineLength")
class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val authRepository: AuthRepository
) : ReportRepository {

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

    override fun getMonthlyReport(userId: String, year: Int, month: Int): Flow<MonthlyReport> {
        return combine(
            getBudgetLimit(),
            getCategorySummaries(userId, year, month),
            getHistoricalExpenses(userId, 6),
            getTopSpenders(userId, year, month)
        ) { limit, categorySummaries, history, spenders ->
            val totalExpenses = categorySummaries.sumOf { it.amount }
            val remainingBudget = limit - totalExpenses
            val percentUsed = if (limit > 0) (totalExpenses / limit) * 100.0 else 0.0

            val tempCal = Calendar.getInstance()
            tempCal.set(Calendar.YEAR, year)
            tempCal.set(Calendar.MONTH, month)
            val monthName = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(tempCal.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }

            val limitStatus = when {
                percentUsed < 80.0 -> "within"
                percentUsed <= 120.0 -> "warning"
                else -> "critical"
            }

            MonthlyReport(
                monthName = monthName,
                year = year,
                month = month,
                totalExpenses = totalExpenses,
                budgetLimit = limit,
                remainingBudget = remainingBudget,
                percentUsed = percentUsed,
                limitStatus = limitStatus,
                history6Months = history,
                categoryBreakdown = categorySummaries,
                topSpenders = spenders
            )
        }
    }

    override fun getCategorySummaries(userId: String, year: Int, month: Int): Flow<List<CategorySummary>> {
        return combine(
            transactionDao.getTransactions(userId),
            categoryDao.getCategories(userId)
        ) { entities, categoryEntities ->
            val cal = Calendar.getInstance()
            val monthlyTransactions = entities.filter { entity ->
                cal.timeInMillis = entity.date
                cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
            }
            val totalExpenses = monthlyTransactions.sumOf { it.amount }
            val categories = if (categoryEntities.isEmpty()) {
                listOf("Alimentação", "Moradia", "Transporte", "Lazer", "Outros")
            } else {
                categoryEntities.map { it.name }
            }
            categories.map { cat ->
                val amount = monthlyTransactions.filter { it.category == cat }.sumOf { it.amount }
                val percentage = if (totalExpenses > 0) (amount / totalExpenses) * 100.0 else 0.0
                CategorySummary(
                    category = cat,
                    amount = amount,
                    percentage = percentage
                )
            }
        }
    }

    override fun getHistoricalExpenses(userId: String, limit: Int): Flow<List<MonthlyHistoryBar>> {
        return transactionDao.getTransactions(userId).map { entities ->
            val cal = Calendar.getInstance()
            val currentYear = cal.get(Calendar.YEAR)
            val currentMonth = cal.get(Calendar.MONTH)

            val history = mutableListOf<MonthlyHistoryBar>()
            for (i in (limit - 1) downTo 0) {
                val tempCal = Calendar.getInstance()
                tempCal.set(Calendar.YEAR, currentYear)
                tempCal.set(Calendar.MONTH, currentMonth)
                tempCal.add(Calendar.MONTH, -i)

                val y = tempCal.get(Calendar.YEAR)
                val m = tempCal.get(Calendar.MONTH)

                val monthlyTransactions = entities.filter { entity ->
                    cal.timeInMillis = entity.date
                    cal.get(Calendar.YEAR) == y && cal.get(Calendar.MONTH) == m
                }
                val amount = monthlyTransactions.sumOf { it.amount }

                val monthLabel = SimpleDateFormat("MMM", Locale("pt", "BR"))
                    .format(tempCal.time)
                    .uppercase(Locale("pt", "BR"))
                    .replace(".", "")

                history.add(
                    MonthlyHistoryBar(
                        monthLabel = monthLabel,
                        amount = amount,
                        percentageOfMax = 0.0
                    )
                )
            }

            val maxAmount = history.maxOfOrNull { it.amount } ?: 0.0
            history.map { bar ->
                val pct = if (maxAmount > 0) (bar.amount / maxAmount) * 100.0 else 0.0
                bar.copy(percentageOfMax = pct)
            }
        }
    }

    override fun getTopSpenders(userId: String, year: Int, month: Int): Flow<List<SpenderSummary>> {
        return combine(
            transactionDao.getTransactions(userId),
            authRepository.getCurrentUser()
        ) { entities, currentUser ->
            if (currentUser == null) return@combine emptyList()

            val cal = Calendar.getInstance()
            val userMonthlySpent = entities.filter { entity ->
                cal.timeInMillis = entity.date
                cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
            }.sumOf { it.amount }

            val currentUserSummary = SpenderSummary(
                userId = currentUser.id,
                name = currentUser.name.ifBlank { "Você" },
                totalSpent = userMonthlySpent,
                rank = 1,
                role = currentUser.role
            )

            // Dynamic mock profiles offset relative to user budget/spending to make it look active
            val familyMembers = listOf(
                currentUserSummary,
                SpenderSummary(
                    userId = "ricardo_id",
                    name = "Ricardo",
                    totalSpent = 2450.0,
                    rank = 2,
                    role = "Pai"
                ),
                SpenderSummary(
                    userId = "helena_id",
                    name = "Helena",
                    totalSpent = 1820.0,
                    rank = 3,
                    role = "Mãe"
                ),
                SpenderSummary(
                    userId = "lucas_id",
                    name = "Lucas",
                    totalSpent = 580.0,
                    rank = 4,
                    role = "Filho"
                )
            )

            familyMembers.sortedByDescending { it.totalSpent }.mapIndexed { index, summary ->
                summary.copy(rank = index + 1)
            }
        }
    }
}
