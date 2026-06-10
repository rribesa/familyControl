package br.com.rribesa.familycontrol.feature.finance.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}
