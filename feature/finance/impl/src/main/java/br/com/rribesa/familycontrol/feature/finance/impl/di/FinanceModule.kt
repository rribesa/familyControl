package br.com.rribesa.familycontrol.feature.finance.impl.di

import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.FinanceRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import br.com.rribesa.familycontrol.feature.finance.impl.data.repository.FinanceRepositoryImpl
import br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase.GetDashboardStats
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FinanceModule {

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl
    ): FinanceRepository
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class FinanceUseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindGetDashboardStatsUseCase(
        impl: GetDashboardStats
    ): GetDashboardStatsUseCase
}
