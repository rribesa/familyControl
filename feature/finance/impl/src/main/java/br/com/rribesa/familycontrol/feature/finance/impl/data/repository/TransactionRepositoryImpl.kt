package br.com.rribesa.familycontrol.feature.finance.impl.data.repository

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.TransactionDao
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.TransactionEntity
import br.com.rribesa.familycontrol.core.data.FirestorePaths
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("TooGenericExceptionCaught", "SwallowedException")
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionRepository {

    override fun getTransactions(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactions(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        withContext(ioDispatcher) {
            val entity = TransactionEntity.fromDomain(transaction, isSynced = false)
            transactionDao.insertTransaction(entity)

            try {
                uploadToFirestore(transaction)
                transactionDao.markSynced(transaction.id.toString())
            } catch (e: Exception) {
                // Ignore exception for offline-first, will sync later
            }
        }
    }

    override suspend fun syncTransactions(userId: String) {
        withContext(ioDispatcher) {
            // 1. Upload unsynced transactions
            val unsynced = transactionDao.getUnsyncedTransactions(userId)
            unsynced.forEach { entity ->
                try {
                    uploadToFirestore(entity.toDomain())
                    transactionDao.markSynced(entity.id)
                } catch (e: Exception) {
                    // If offline, continue
                }
            }

            // 2. Download remote transactions from Firestore
            try {
                val snapshot = firestore.collection(FirestorePaths.TRANSACTIONS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val remoteTransactions = snapshot.documents.mapNotNull { doc ->
                    val idStr = doc.getString("id") ?: return@mapNotNull null
                    val amount = doc.getDouble("amount") ?: return@mapNotNull null
                    val category = doc.getString("category") ?: return@mapNotNull null
                    val date = doc.getLong("date") ?: return@mapNotNull null
                    val description = doc.getString("description") ?: return@mapNotNull null
                    val uid = doc.getString("userId") ?: return@mapNotNull null

                    TransactionEntity(
                        id = idStr,
                        amount = amount,
                        category = category,
                        date = date,
                        description = description,
                        userId = uid,
                        isSynced = true
                    )
                }
                if (remoteTransactions.isNotEmpty()) {
                    transactionDao.insertTransactions(remoteTransactions)
                }
            } catch (e: Exception) {
                // Ignore download failures if offline
            }
        }
    }

    private suspend fun uploadToFirestore(transaction: Transaction) {
        val data = mapOf(
            "id" to transaction.id.toString(),
            "amount" to transaction.amount,
            "category" to transaction.category,
            "date" to transaction.date,
            "description" to transaction.description,
            "userId" to transaction.userId
        )
        firestore.collection(FirestorePaths.TRANSACTIONS)
            .document(transaction.id.toString())
            .set(data)
            .await()
    }
}
