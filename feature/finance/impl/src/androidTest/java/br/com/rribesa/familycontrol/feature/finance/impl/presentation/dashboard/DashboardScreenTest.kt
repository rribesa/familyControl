package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_displaysAllCoreComponents() {
        val state = DashboardState(
            budgetStats = BudgetStats(
                totalIncome = 5000.0,
                totalExpenses = 3500.0,
                budgetLimit = 4000.0
            )
        )
        composeTestRule.setContent {
            FamilyControlTheme {
                DashboardScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Visão Geral").assertIsDisplayed()
        composeTestRule.onNodeWithText("Receitas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Despesas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saldo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Limite do Orçamento").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ver Relatório Completo").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_displaysHealthyStatus_whenExpensesBelow80Percent() {
        val state = DashboardState(
            budgetStats = BudgetStats(
                totalIncome = 1000.0,
                totalExpenses = 70.0,
                budgetLimit = 100.0
            )
        )
        composeTestRule.setContent {
            FamilyControlTheme {
                DashboardScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Dentro do orçamento").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_displaysWarningStatus_whenExpensesBetween80And120Percent() {
        val state = DashboardState(
            budgetStats = BudgetStats(
                totalIncome = 1000.0,
                totalExpenses = 95.0,
                budgetLimit = 100.0
            )
        )
        composeTestRule.setContent {
            FamilyControlTheme {
                DashboardScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Próximo ao limite").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_displaysCriticalStatus_whenExpensesExceed120Percent() {
        val state = DashboardState(
            budgetStats = BudgetStats(
                totalIncome = 1000.0,
                totalExpenses = 125.0,
                budgetLimit = 100.0
            )
        )
        composeTestRule.setContent {
            FamilyControlTheme {
                DashboardScreen(
                    state = state,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Orçamento estourado").assertIsDisplayed()
    }
}
