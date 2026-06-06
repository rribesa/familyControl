package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetTransactionHistoryUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.SyncTransactionsUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase = mockk(relaxed = true)
    private val syncTransactionsUseCase: SyncTransactionsUseCase = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: HistoryViewModel

    private val mockTransactions = listOf(
        Transaction(UUID.randomUUID(), 1000.0, "Salário", System.currentTimeMillis(), "Salary", "user1"),
        Transaction(UUID.randomUUID(), 50.0, "Alimentação", System.currentTimeMillis(), "Mercado", "user1"),
        Transaction(UUID.randomUUID(), 20.0, "Lazer", System.currentTimeMillis(), "Cinema", "user1")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getCurrentUser() } returns flowOf(User("user1", "user@test.com", "Test User"))
        every { getTransactionHistoryUseCase("user1") } returns flowOf(mockTransactions)
        viewModel = HistoryViewModel(getTransactionHistoryUseCase, syncTransactionsUseCase, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load history on start should load list and trigger sync`() {
        val state = viewModel.state.value
        assertEquals(3, state.transactions.size)
        assertEquals(3, state.filteredTransactions.size)
        coVerify(exactly = 1) { syncTransactionsUseCase("user1") }
    }

    @Test
    fun `filter change to income should return only salary transactions`() {
        viewModel.onEvent(HistoryEvent.OnFilterChanged(HistoryFilter.INCOME))
        
        val state = viewModel.state.value
        assertEquals(HistoryFilter.INCOME, state.filter)
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("Salário", state.filteredTransactions[0].category)
    }

    @Test
    fun `filter change to expense should return other transactions`() {
        viewModel.onEvent(HistoryEvent.OnFilterChanged(HistoryFilter.EXPENSE))

        val state = viewModel.state.value
        assertEquals(HistoryFilter.EXPENSE, state.filter)
        assertEquals(2, state.filteredTransactions.size)
        assertTrue(state.filteredTransactions.all { it.category != "Salário" })
    }

    @Test
    fun `refresh event should trigger sync usecase`() = runTest {
        viewModel.onEvent(HistoryEvent.OnRefresh)
        coVerify(atLeast = 1) { syncTransactionsUseCase("user1") }
    }
}
