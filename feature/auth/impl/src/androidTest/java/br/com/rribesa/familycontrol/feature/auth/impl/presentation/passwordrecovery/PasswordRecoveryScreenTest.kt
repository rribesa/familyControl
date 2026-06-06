package br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.core.ui.R
import org.junit.Rule
import org.junit.Test

class PasswordRecoveryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun passwordRecoveryScreen_displaysAllCoreComponents() {
        val state = PasswordRecoveryState()
        composeTestRule.setContent {
            FamilyControlTheme {
                PasswordRecoveryScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Recuperar Senha").assertIsDisplayed()
        composeTestRule.onNodeWithText("E-mail").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enviar Link").assertIsDisplayed()
        composeTestRule.onNodeWithText("Voltar para o Login").assertIsDisplayed()
    }

    @Test
    fun passwordRecoveryScreen_showsEmailError_whenStateHasEmailError() {
        val state = PasswordRecoveryState(emailErrorResId = R.string.error_invalid_email)
        composeTestRule.setContent {
            FamilyControlTheme {
                PasswordRecoveryScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("E-mail inválido").assertIsDisplayed()
    }

    @Test
    fun passwordRecoveryScreen_showsSuccessBanner_whenStateIsSuccess() {
        val state = PasswordRecoveryState(isSuccess = true)
        composeTestRule.setContent {
            FamilyControlTheme {
                PasswordRecoveryScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Link de recuperação enviado com sucesso para o seu e-mail.").assertIsDisplayed()
    }

    @Test
    fun passwordRecoveryScreen_showsLoadingIndicator_whenStateIsLoading() {
        val state = PasswordRecoveryState(isLoading = true)
        composeTestRule.setContent {
            FamilyControlTheme {
                PasswordRecoveryScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Enviar Link").assertDoesNotExist()
    }
}
