package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.core.ui.R
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllCoreComponents() {
        val state = LoginState()
        composeTestRule.setContent {
            FamilyControlTheme {
                LoginScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Controle da Família").assertIsDisplayed()
        composeTestRule.onNodeWithText("E-mail").assertIsDisplayed()
        composeTestRule.onNodeWithText("Senha").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entrar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continuar com Google").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsEmailError_whenStateHasEmailError() {
        val state = LoginState(emailErrorResId = R.string.error_invalid_email)
        composeTestRule.setContent {
            FamilyControlTheme {
                LoginScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("E-mail inválido").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsLoadingIndicator_whenStateIsLoading() {
        val state = LoginState(isLoading = true)
        composeTestRule.setContent {
            FamilyControlTheme {
                LoginScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entrar").assertDoesNotExist()
    }
}
