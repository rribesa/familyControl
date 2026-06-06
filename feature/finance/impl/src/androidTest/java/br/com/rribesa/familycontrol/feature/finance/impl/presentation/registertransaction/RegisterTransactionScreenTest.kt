package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class RegisterTransactionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayRegisterInputs() {
        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterTransactionScreen(
                    state = RegisterTransactionState(),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Valor").assertIsDisplayed()
        composeTestRule.onNodeWithText("Categoria").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descrição").assertIsDisplayed()
        composeTestRule.onNodeWithText("Data").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salvar Transação").assertIsDisplayed()
    }

    @Test
    fun shouldTriggerCategoryChangedEventOnChipClick() {
        val onEventMock = mockk<(RegisterTransactionEvent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterTransactionScreen(
                    state = RegisterTransactionState(),
                    onEvent = onEventMock
                )
            }
        }

        composeTestRule.onNodeWithText("Alimentação").performClick()

        verify { onEventMock(RegisterTransactionEvent.OnCategoryChanged("Alimentação")) }
    }

    @Test
    fun shouldDisplayErrorMessage() {
        composeTestRule.setContent {
            FamilyControlTheme {
                RegisterTransactionScreen(
                    state = RegisterTransactionState(
                        errorMessageResId = br.com.rribesa.familycontrol.core.ui.R.string.transactions_error_amount
                    ),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Valor deve ser maior que zero").assertIsDisplayed()
    }
}
