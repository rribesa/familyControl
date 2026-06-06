package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

sealed interface DashboardEvent {
    data object OnRefresh : DashboardEvent
    data object OnReportClicked : DashboardEvent
    data object OnAddTransactionClicked : DashboardEvent
    data object OnViewHistoryClicked : DashboardEvent
}
