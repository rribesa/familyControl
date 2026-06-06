package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.ForgotPasswordUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordRecoveryViewModelTest {

    private val forgotPasswordUseCase: ForgotPasswordUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: PasswordRecoveryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PasswordRecoveryViewModel(
            forgotPasswordUseCase = forgotPasswordUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isCorrect() {
        val state = viewModel.state.value
        assertEquals("", state.email)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.emailErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onEmailChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(PasswordRecoveryEvent.OnEmailChanged("recovery@domain.com"))

        val state = viewModel.state.value
        assertEquals("recovery@domain.com", state.email)
        assertNull(state.emailErrorResId)
        assertNull(state.errorMessageResId)
        assertFalse(state.isSuccess)
    }

    @Test
    fun onBackToLoginClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<PasswordRecoveryEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(PasswordRecoveryEvent.OnBackToLoginClicked)

        assertEquals(1, effects.size)
        assertEquals(PasswordRecoveryEffect.NavigateToLogin, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onSendClicked_withEmptyEmail_setsEmptyError() {
        viewModel.onEvent(PasswordRecoveryEvent.OnSendClicked)

        assertEquals(R.string.error_empty_field, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onSendClicked_withInvalidEmail_setsInvalidError() {
        viewModel.onEvent(PasswordRecoveryEvent.OnEmailChanged("invalid-email"))
        viewModel.onEvent(PasswordRecoveryEvent.OnSendClicked)

        assertEquals(R.string.error_invalid_email, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onSendClicked_withValidEmail_success_updatesStateToSuccess() = runTest {
        val email = "valid@email.com"
        viewModel.onEvent(PasswordRecoveryEvent.OnEmailChanged(email))

        coEvery { forgotPasswordUseCase(email) } returns Unit

        viewModel.onEvent(PasswordRecoveryEvent.OnSendClicked)

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.emailErrorResId)
        assertNull(viewModel.state.value.errorMessageResId)

        coVerify(exactly = 1) { forgotPasswordUseCase(email) }
    }

    @Test
    fun onSendClicked_withValidEmail_failure_showsError() = runTest {
        val email = "valid@email.com"
        viewModel.onEvent(PasswordRecoveryEvent.OnEmailChanged(email))

        coEvery { forgotPasswordUseCase(email) } throws RuntimeException("Reset failed")

        val effects = mutableListOf<PasswordRecoveryEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(PasswordRecoveryEvent.OnSendClicked)

        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isSuccess)
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(PasswordRecoveryEffect.ShowError(R.string.error_generic), effects[0])
        collectJob.cancel()
    }
}
