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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExportOptionsViewModelTest {

    private val getMonthlyReportUseCase: GetMonthlyReportUseCase = mockk()
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ExportOptionsViewModel

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
        viewModel = ExportOptionsViewModel(getMonthlyReportUseCase, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsPreviewStats() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockReport, state.monthlyReport)
    }

    @Test
    fun onBackClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<ExportOptionsEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(ExportOptionsEvent.OnBackClicked)

        assertEquals(1, effects.size)
        assertEquals(ExportOptionsEffect.NavigateBack, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onPeriodSelected_updatesState() {
        viewModel.onEvent(ExportOptionsEvent.OnPeriodSelected("previous"))
        assertEquals("previous", viewModel.state.value.selectedPeriod)
    }

    @Test
    fun onFormatSelected_updatesState() {
        viewModel.onEvent(ExportOptionsEvent.OnFormatSelected("excel"))
        assertEquals("excel", viewModel.state.value.selectedFormat)
    }

    @Test
    fun onGenerateClicked_simulatesExportSuccess() = runTest {
        viewModel.onEvent(ExportOptionsEvent.OnGenerateClicked)
        testScheduler.advanceTimeBy(1600)
        testScheduler.runCurrent()
        assertTrue(viewModel.state.value.exportSuccess)
    }
}
