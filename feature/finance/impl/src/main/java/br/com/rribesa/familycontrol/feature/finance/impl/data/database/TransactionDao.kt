package br.com.rribesa.familycontrol.feature.finance.impl.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isSynced = 0 AND userId = :userId")
    fun getUnsyncedTransactions(userId: String): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<TransactionEntity>): List<Long>

    @Query("UPDATE transactions SET isSynced = 1 WHERE id = :id")
    fun markSynced(id: String): Int
}
