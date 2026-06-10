package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

sealed interface ReportEvent {
    data object OnBackClicked : ReportEvent
    data object OnExportClicked : ReportEvent
    data class OnMonthSelected(val year: Int, val month: Int) : ReportEvent
}
