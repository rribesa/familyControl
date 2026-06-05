package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.RegisterUserUseCase
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
class RegisterViewModelTest {

    private val registerUserUseCase: RegisterUserUseCase = mockk()
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(
            registerUserUseCase = registerUserUseCase,
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
        assertEquals("", state.fullName)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isLoading)
        assertNull(state.fullNameErrorResId)
        assertNull(state.emailErrorResId)
        assertNull(state.passwordErrorResId)
        assertNull(state.confirmPasswordErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onFullNameChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(RegisterEvent.OnFullNameChanged("John Doe"))
        val state = viewModel.state.value
        assertEquals("John Doe", state.fullName)
        assertNull(state.fullNameErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onEmailChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(RegisterEvent.OnEmailChanged("john@domain.com"))
        val state = viewModel.state.value
        assertEquals("john@domain.com", state.email)
        assertNull(state.emailErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onPasswordChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("Strong1#"))
        val state = viewModel.state.value
        assertEquals("Strong1#", state.password)
        assertNull(state.passwordErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun onConfirmPasswordChanged_updatesStateAndClearsErrors() {
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("Strong1#"))
        val state = viewModel.state.value
        assertEquals("Strong1#", state.confirmPassword)
        assertNull(state.confirmPasswordErrorResId)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun togglePasswordVisibility_updatesState() {
        assertFalse(viewModel.state.value.isPasswordVisible)
        viewModel.onEvent(RegisterEvent.TogglePasswordVisibility)
        assertTrue(viewModel.state.value.isPasswordVisible)
    }

    @Test
    fun onRegisterClicked_withEmptyFullName_setsError() {
        viewModel.onEvent(RegisterEvent.OnEmailChanged("john@email.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(R.string.error_empty_field, viewModel.state.value.fullNameErrorResId)
    }

    @Test
    fun onRegisterClicked_withEmptyEmail_setsError() {
        viewModel.onEvent(RegisterEvent.OnFullNameChanged("John Doe"))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(R.string.error_empty_field, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onRegisterClicked_withInvalidEmail_setsError() {
        viewModel.onEvent(RegisterEvent.OnFullNameChanged("John Doe"))
        viewModel.onEvent(RegisterEvent.OnEmailChanged("invalid-email"))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(R.string.error_invalid_email, viewModel.state.value.emailErrorResId)
    }

    @Test
    fun onRegisterClicked_withInvalidPassword_setsError() {
        viewModel.onEvent(RegisterEvent.OnFullNameChanged("John Doe"))
        viewModel.onEvent(RegisterEvent.OnEmailChanged("john@email.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("short"))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("short"))
        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(R.string.error_invalid_password, viewModel.state.value.errorMessageResId)
    }

    @Test
    fun onRegisterClicked_withPasswordMismatch_setsError() {
        viewModel.onEvent(RegisterEvent.OnFullNameChanged("John Doe"))
        viewModel.onEvent(RegisterEvent.OnEmailChanged("john@email.com"))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged("Strong1#"))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged("Strong2#"))
        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(R.string.error_password_mismatch, viewModel.state.value.errorMessageResId)
    }

    @Test
    fun onRegisterClicked_success_clearsFieldsAndNavigates() = runTest {
        val name = "John Doe"
        val email = "john@email.com"
        val password = "Strong1#"
        
        viewModel.onEvent(RegisterEvent.OnFullNameChanged(name))
        viewModel.onEvent(RegisterEvent.OnEmailChanged(email))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged(password))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged(password))

        coEvery { registerUserUseCase(email, name, password) } returns User("123", email, name)

        val effects = mutableListOf<RegisterEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals("", viewModel.state.value.fullName)
        assertEquals("", viewModel.state.value.email)
        assertEquals("", viewModel.state.value.password)
        assertEquals("", viewModel.state.value.confirmPassword)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(RegisterEffect.NavigateToDashboard, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onRegisterClicked_failure_retainsFieldsAndShowsError() = runTest {
        val name = "John Doe"
        val email = "john@email.com"
        val password = "Strong1#"
        
        viewModel.onEvent(RegisterEvent.OnFullNameChanged(name))
        viewModel.onEvent(RegisterEvent.OnEmailChanged(email))
        viewModel.onEvent(RegisterEvent.OnPasswordChanged(password))
        viewModel.onEvent(RegisterEvent.OnConfirmPasswordChanged(password))

        coEvery { registerUserUseCase(email, name, password) } throws RuntimeException("Registration failed")

        val effects = mutableListOf<RegisterEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(RegisterEvent.OnRegisterClicked)

        assertEquals(name, viewModel.state.value.fullName)
        assertEquals(email, viewModel.state.value.email)
        assertEquals(password, viewModel.state.value.password)
        assertEquals(password, viewModel.state.value.confirmPassword)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(RegisterEffect.ShowError(R.string.error_generic), effects[0])
        collectJob.cancel()
    }

    @Test
    fun onGoogleRegisterClicked_success_clearsFieldsAndNavigates() = runTest {
        val idToken = "google_id_token"
        coEvery { loginWithGoogleUseCase(idToken) } returns User("123", "google@email.com", "Google User")

        val effects = mutableListOf<RegisterEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(RegisterEvent.OnGoogleRegisterClicked(idToken))

        assertEquals("", viewModel.state.value.fullName)
        assertEquals("", viewModel.state.value.email)
        assertEquals("", viewModel.state.value.password)
        assertEquals("", viewModel.state.value.confirmPassword)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(RegisterEffect.NavigateToDashboard, effects[0])
        collectJob.cancel()
    }

    @Test
    fun onGoogleRegisterClicked_failure_showsError() = runTest {
        val idToken = "google_id_token"
        coEvery { loginWithGoogleUseCase(idToken) } throws RuntimeException("Google auth failed")

        val effects = mutableListOf<RegisterEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(RegisterEvent.OnGoogleRegisterClicked(idToken))

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.error_generic, viewModel.state.value.errorMessageResId)

        assertEquals(1, effects.size)
        assertEquals(RegisterEffect.ShowError(R.string.error_generic), effects[0])
        collectJob.cancel()
    }
}
