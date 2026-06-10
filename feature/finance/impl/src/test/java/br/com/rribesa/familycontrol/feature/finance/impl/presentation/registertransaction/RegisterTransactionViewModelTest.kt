package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.AddTransactionUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetCategoriesUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.ManageCategoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
    private val getCategoriesUseCase: GetCategoriesUseCase = mockk(relaxed = true)
    private val manageCategoriesUseCase: ManageCategoriesUseCase = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: RegisterTransactionViewModel

    private val mockCategories = listOf(
        Category(name = "Alimentação", userId = "user1"),
        Category(name = "Lazer", userId = "user1")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getCurrentUser() } returns flowOf(
            User("user1", "user@test.com", "Test User", "Editor")
        )
        coEvery { getCategoriesUseCase("user1") } returns flowOf(mockCategories)
        viewModel = RegisterTransactionViewModel(
            addTransactionUseCase,
            authRepository,
            getCategoriesUseCase,
            manageCategoriesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load categories`() {
        val state = viewModel.state.value
        assertEquals("", state.amount)
        assertEquals("", state.category)
        assertEquals("", state.description)
        assertNull(state.errorMessageResId)
        assertEquals(2, state.categoriesList.size)
        assertEquals("Alimentação", state.categoriesList[0])
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
        assertEquals("", viewModel.state.value.amount)
        assertEquals("", viewModel.state.value.category)
        assertEquals("", viewModel.state.value.description)
        assertTrue(effects.contains(RegisterTransactionEffect.ShowSuccessMessage))
        assertTrue(effects.contains(RegisterTransactionEffect.NavigateBack))

        effectJob.cancel()
    }

    @Test
    fun `save transaction with Viewer role should fail`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns flowOf(
            User("user1", "user@test.com", "Test User", "Viewer")
        )
        
        viewModel.onEvent(RegisterTransactionEvent.OnAmountChanged("250.00"))
        viewModel.onEvent(RegisterTransactionEvent.OnCategoryChanged("Alimentação"))
        viewModel.onEvent(RegisterTransactionEvent.OnSaveClicked)

        assertEquals(R.string.error_permission_denied, viewModel.state.value.errorMessageResId)
        coVerify(exactly = 0) { addTransactionUseCase(any()) }
    }

    @Test
    fun `add custom category with Editor role should succeed`() = runTest {
        viewModel.onEvent(RegisterTransactionEvent.OnNewCategoryNameChanged("Transporte"))
        viewModel.onEvent(RegisterTransactionEvent.OnAddCustomCategoryClicked)

        coVerify(exactly = 1) { manageCategoriesUseCase(any()) }
        assertEquals("", viewModel.state.value.newCategoryName)
    }

    @Test
    fun `add custom category with Viewer role should fail`() = runTest {
        coEvery { authRepository.getCurrentUser() } returns flowOf(
            User("user1", "user@test.com", "Test User", "Viewer")
        )
        
        viewModel.onEvent(RegisterTransactionEvent.OnNewCategoryNameChanged("Transporte"))
        viewModel.onEvent(RegisterTransactionEvent.OnAddCustomCategoryClicked)

        coVerify(exactly = 0) { manageCategoriesUseCase(any()) }
        assertEquals(R.string.error_permission_denied, viewModel.state.value.errorMessageResId)
    }
}
