package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.HealthStatus
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.DashboardStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val getDashboardStatsUseCase: GetDashboardStatsUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: DashboardViewModel

    private val defaultStats = DashboardStats(
        budgetStats = BudgetStats(5000.0, 3500.0, 4000.0),
        categorySummaries = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getDashboardStatsUseCase() } returns flowOf(defaultStats)
        viewModel = DashboardViewModel(getDashboardStatsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsDataSuccessfully() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(defaultStats.budgetStats, state.budgetStats)
    }

    @Test
    fun onReportClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<DashboardEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(DashboardEvent.OnReportClicked)

        assertEquals(1, effects.size)
        assertEquals(DashboardEffect.NavigateToReport, effects[0])
        collectJob.cancel()
    }

    @Test
    fun budgetStats_healthStatusThresholds_areCorrect() {
        // Healthy status (< 80%)
        val healthyStats = BudgetStats(totalIncome = 1000.0, totalExpenses = 79.0, budgetLimit = 100.0)
        assertEquals(HealthStatus.HEALTHY, healthyStats.healthStatus)

        // Warning status (80% - 120%)
        val warningStatsMin = BudgetStats(totalIncome = 1000.0, totalExpenses = 80.0, budgetLimit = 100.0)
        val warningStatsMax = BudgetStats(totalIncome = 1000.0, totalExpenses = 120.0, budgetLimit = 100.0)
        assertEquals(HealthStatus.WARNING, warningStatsMin.healthStatus)
        assertEquals(HealthStatus.WARNING, warningStatsMax.healthStatus)

        // Critical status (> 120%)
        val criticalStats = BudgetStats(totalIncome = 1000.0, totalExpenses = 121.0, budgetLimit = 100.0)
        assertEquals(HealthStatus.CRITICAL, criticalStats.healthStatus)
    }
}
