package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.core.ui.R
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_displaysAllCoreComponents() {
        val state = RegisterState()
        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {},
                    onLoginClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Criar Conta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nome Completo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Senha Forte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirmar Senha").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cadastrar").assertIsDisplayed()
    }

    @Test
    fun registerScreen_showsPasswordError_whenPasswordIsInvalid() {
        val state = RegisterState(passwordErrorResId = R.string.error_invalid_password)
        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {},
                    onLoginClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithText("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula, um número e um caractere especial.").assertIsDisplayed()
    }

    @Test
    fun registerScreen_showsMismatchError_whenPasswordsMismatch() {
        val state = RegisterState(confirmPasswordErrorResId = R.string.error_password_mismatch)
        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterScreen(
                    state = state,
                    webClientId = "",
                    onEvent = {},
                    onLoginClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithText("As senhas não coincidem").assertIsDisplayed()
    }
}
