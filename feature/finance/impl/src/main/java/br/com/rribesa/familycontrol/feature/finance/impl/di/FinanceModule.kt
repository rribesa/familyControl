package br.com.rribesa.familycontrol.feature.finance.impl.di

import android.content.Context
import androidx.room.Room
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AddTransactionUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetTransactionHistoryUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.SyncTransactionsUseCase
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.FinanceDatabase
import br.com.rribesa.familycontrol.feature.finance.impl.data.database.TransactionDao
import br.com.rribesa.familycontrol.feature.finance.impl.data.repository.FinanceRepositoryImpl
import br.com.rribesa.familycontrol.feature.finance.impl.data.repository.TransactionRepositoryImpl
import br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase.GetDashboardStats
import br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase.AddTransaction
import br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase.GetTransactionHistory
import br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase.SyncTransactions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FinanceModule {

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl
    ): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindGetDashboardStatsUseCase(
        impl: GetDashboardStats
    ): GetDashboardStatsUseCase

    @Binds
    @Singleton
    abstract fun bindAddTransactionUseCase(
        impl: AddTransaction
    ): AddTransactionUseCase

    @Binds
    @Singleton
    abstract fun bindGetTransactionHistoryUseCase(
        impl: GetTransactionHistory
    ): GetTransactionHistoryUseCase

    @Binds
    @Singleton
    abstract fun bindSyncTransactionsUseCase(
        impl: SyncTransactions
    ): SyncTransactionsUseCase

    companion object {
        @Provides
        @Singleton
        fun provideFinanceDatabase(
            @ApplicationContext context: Context
        ): FinanceDatabase {
            return Room.databaseBuilder(
                context,
                FinanceDatabase::class.java,
                "finance_database"
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        @Singleton
        fun provideTransactionDao(
            database: FinanceDatabase
        ): TransactionDao {
            return database.transactionDao()
        }

        @Provides
        @Singleton
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
