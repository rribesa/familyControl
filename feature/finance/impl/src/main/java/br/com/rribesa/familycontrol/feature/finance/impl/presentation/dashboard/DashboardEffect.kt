package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

sealed interface DashboardEffect {
    data object NavigateToReport : DashboardEffect
    data object NavigateToRegisterTransaction : DashboardEffect
    data object NavigateToHistory : DashboardEffect
}
