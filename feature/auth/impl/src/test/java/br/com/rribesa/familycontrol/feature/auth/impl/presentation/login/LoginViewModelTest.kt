package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithEmailUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithGoogleUseCase
import io.mockk.coEvery
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
class LoginViewModelTest {

    private val loginWithEmailUseCase: LoginWithEmailUseCase = mockk()
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(
            loginWithEmailUseCase = loginWithEmailUseCase,
            loginWithGoogleUseCase = loginWithGoogleUseCase
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
        assertEquals("", state.password)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isLoading)
        assertNull(state.emailErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onEmailChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test@domain.com"))
        
        val state = viewModel.state.value
        assertEquals("test@domain.com", state.email)
        assertNull(state.emailErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onPasswordChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(LoginEvent.OnPasswordChanged("password123"))
        
        val state = viewModel.state.value
        assertEquals("password123", state.password)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun togglePasswordVisibility_updatesState() {
        assertFalse(viewModel.state.value.isPasswordVisible)
        
        viewModel.onEvent(LoginEvent.TogglePasswordVisibility)
        assertTrue(viewModel.state.value.isPasswordVisible)
        
        viewModel.onEvent(LoginEvent.TogglePasswordVisibility)
        assertFalse(viewModel.state.value.isPasswordVisible)
    }

    @Test
    fun onForgotPasswordClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnForgotPasswordClicked)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.NavigateToForgotPassword, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onRegisterClicked_emitsCorrectEffect() = runTest {
        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnRegisterClicked)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.NavigateToRegister, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onLoginClicked_withEmptyEmail_setsEmptyError() {
        viewModel.onEvent(LoginEvent.OnPasswordChanged("ValidPass123"))
        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertEquals(R.string.error_empty_field, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onLoginClicked_withInvalidEmail_setsInvalidError() {
        viewModel.onEvent(LoginEvent.OnEmailChanged("invalid-email"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("ValidPass123"))
        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertEquals(R.string.error_invalid_email, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onLoginClicked_withEmptyPassword_setsEmptyError() {
        viewModel.onEvent(LoginEvent.OnEmailChanged("valid@email.com"))
        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertEquals(R.string.error_empty_field, viewModel.state.value.errorMessageResId)
    }

    @Test
    fun onLoginClicked_withValidCredentials_success_clearsFieldsAndNavigates() = runTest {
        val email = "valid@email.com"
        val password = "ValidPassword1"
        viewModel.onEvent(LoginEvent.OnEmailChanged(email))
        viewModel.onEvent(LoginEvent.OnPasswordChanged(password))

        coEvery { loginWithEmailUseCase(email, password) } returns User("123", email, "Name")

        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertEquals("", viewModel.state.value.email)
        assertEquals("", viewModel.state.value.password)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.NavigateToDashboard, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onLoginClicked_withValidCredentials_failure_retainsFieldsAndShowsError() = runTest {
        val email = "valid@email.com"
        val password = "ValidPassword1"
        viewModel.onEvent(LoginEvent.OnEmailChanged(email))
        viewModel.onEvent(LoginEvent.OnPasswordChanged(password))

        coEvery { loginWithEmailUseCase(email, password) } throws RuntimeException("Auth failed")

        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertEquals(email, viewModel.state.value.email)
        assertEquals(password, viewModel.state.value.password)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.ShowError(R.string.error_generic), effects[0])
        collectJob.cancel()
    }

    @Test
    fun onGoogleLoginClicked_success_clearsFieldsAndNavigates() = runTest {
        val idToken = "google_id_token"
        coEvery { loginWithGoogleUseCase(idToken) } returns User("123", "google@email.com", "Google User")

        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnGoogleLoginClicked(idToken))

        assertEquals("", viewModel.state.value.email)
        assertEquals("", viewModel.state.value.password)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.NavigateToDashboard, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onGoogleLoginClicked_failure_showsError() = runTest {
        val idToken = "google_id_token"
        coEvery { loginWithGoogleUseCase(idToken) } throws RuntimeException("Google auth failed")

        val effects = mutableListOf<LoginEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(LoginEvent.OnGoogleLoginClicked(idToken))

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(LoginEffect.ShowError(R.string.error_generic), effects[0])
        collectJob.cancel()
    }
}
