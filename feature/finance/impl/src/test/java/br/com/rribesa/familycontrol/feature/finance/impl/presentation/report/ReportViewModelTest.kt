package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.MonthlyReport
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetMonthlyReportUseCase
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
class ReportViewModelTest {

    private val getMonthlyReportUseCase: GetMonthlyReportUseCase = mockk()
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ReportViewModel

    private val mockReport = MonthlyReport(
        monthName = "Outubro 2023",
        year = 2023,
        month = 9,
        totalExpenses = 4850.0,
        budgetLimit = 6000.0,
        remainingBudget = 1150.0,
        percentUsed = 81.0,
        limitStatus = "within",
        history6Months = emptyList(),
        categoryBreakdown = emptyList(),
        topSpenders = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getMonthlyReportUseCase(any(), any(), any()) } returns flowOf(mockReport)
        coEvery { authRepository.getCurrentUser() } returns flowOf(User("1", "test@test.com", "Test User", "Editor"))
        viewModel = ReportViewModel(getMonthlyReportUseCase, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsDataSuccessfully() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockReport, state.monthlyReport)
    }

    @Test
    fun onBackClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<ReportEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(ReportEvent.OnBackClicked)

        assertEquals(1, effects.size)
        assertEquals(ReportEffect.NavigateBack, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onExportClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<ReportEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(ReportEvent.OnExportClicked)

        assertEquals(1, effects.size)
        assertEquals(ReportEffect.NavigateToExportOptions, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onMonthSelected_loadsNewReport() = runTest {
        viewModel.onEvent(ReportEvent.OnMonthSelected(2023, 8))
        assertEquals(2023, viewModel.state.value.selectedYear)
        assertEquals(8, viewModel.state.value.selectedMonth)
    }
}
