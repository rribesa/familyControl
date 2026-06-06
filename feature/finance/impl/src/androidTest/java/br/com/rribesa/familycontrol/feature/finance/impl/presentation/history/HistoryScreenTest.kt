package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayFiltersAndHeader() {
        composeTestRule.setContent {
            FamilyControlTheme {
                HistoryScreen(
                    state = HistoryState(),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Histórico de Transações").assertIsDisplayed()
        composeTestRule.onNodeWithText("Todas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Receitas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Despesas").assertIsDisplayed()
    }

    @Test
    fun shouldTriggerFilterChangedEventOnClick() {
        val onEventMock = mockk<(HistoryEvent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FamilyControlTheme {
                HistoryScreen(
                    state = HistoryState(),
                    onEvent = onEventMock
                )
            }
        }

        composeTestRule.onNodeWithText("Receitas").performClick()

        verify { onEventMock(HistoryEvent.OnFilterChanged(HistoryFilter.INCOME)) }
    }

    @Test
    fun shouldDisplayEmptyState() {
        composeTestRule.setContent {
            FamilyControlTheme {
                HistoryScreen(
                    state = HistoryState(transactions = emptyList(), filteredTransactions = emptyList()),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nenhuma transação encontrada").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayTransactionItems() {
        val transaction = Transaction(
            id = UUID.randomUUID(),
            amount = 150.75,
            category = "Alimentação",
            date = System.currentTimeMillis(),
            description = "Mercado Semanal",
            userId = "user1"
        )

        composeTestRule.setContent {
            FamilyControlTheme {
                HistoryScreen(
                    state = HistoryState(
                        transactions = listOf(transaction),
                        filteredTransactions = listOf(transaction)
                    ),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Mercado Semanal").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alimentação").assertIsDisplayed()
        composeTestRule.onNodeWithText("- R$ 150,75").assertIsDisplayed()
    }
}
