package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AddTransactionUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterTransactionViewModelTest {

    private val addTransactionUseCase: AddTransactionUseCase = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: RegisterTransactionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getCurrentUser() } returns flowOf(User("user1", "user@test.com", "Test User"))
        viewModel = RegisterTransactionViewModel(addTransactionUseCase, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() {
        val state = viewModel.state.value
        assertEquals("", state.amount)
        assertEquals("", state.category)
        assertEquals("", state.description)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `event amount changed should update state`() {
        viewModel.onEvent(RegisterTransactionEvent.OnAmountChanged("150.0"))
        assertEquals("150.0", viewModel.state.value.amount)
    }

    @Test
    fun `save with invalid amount should set error`() = runTest {
        viewModel.onEvent(RegisterTransactionEvent.OnAmountChanged("-10.0"))
        viewModel.onEvent(RegisterTransactionEvent.OnCategoryChanged("Lazer"))
        
        viewModel.onEvent(RegisterTransactionEvent.OnSaveClicked)

        assertEquals(R.string.transactions_error_amount, viewModel.state.value.errorMessageResId)
        coVerify(exactly = 0) { addTransactionUseCase(any()) }
    }

    @Test
    fun `save with empty category should set error`() = runTest {
        viewModel.onEvent(RegisterTransactionEvent.OnAmountChanged("50.0"))
        viewModel.onEvent(RegisterTransactionEvent.OnCategoryChanged(""))

        viewModel.onEvent(RegisterTransactionEvent.OnSaveClicked)

        assertEquals(R.string.transactions_error_category, viewModel.state.value.errorMessageResId)
        coVerify(exactly = 0) { addTransactionUseCase(any()) }
    }

    @Test
    fun `save with valid inputs should trigger use case and success effect`() = runTest {
        viewModel.onEvent(RegisterTransactionEvent.OnAmountChanged("250.00"))
        viewModel.onEvent(RegisterTransactionEvent.OnCategoryChanged("Alimentação"))
        viewModel.onEvent(RegisterTransactionEvent.OnDescriptionChanged("Compras"))

        val effects = mutableListOf<RegisterTransactionEffect>()
        val effectJob = launch(testDispatcher) {
            viewModel.effect.toList(effects)
        }

        viewModel.onEvent(RegisterTransactionEvent.OnSaveClicked)

        coVerify(exactly = 1) { addTransactionUseCase(any()) }
        assertTrue(viewModel.state.value.success)
        assertTrue(effects.contains(RegisterTransactionEffect.ShowSuccessMessage))
        assertTrue(effects.contains(RegisterTransactionEffect.NavigateBack))

        effectJob.cancel()
    }
}
